package org.ray.housewebscraper.util


import org.jsoup.nodes.Attribute
import org.jsoup.nodes.Attributes
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import org.jsoup.select.NodeTraversor

/*
    TODO: nodetraverser object instantiation with possible filters of which the results can be appended to lists or something

 */
//fun main() {
//
//    val scraper = traversor {
//        root = Elements(1)
//        filter {
//            attribute {
//                cssAttributeKey = "data-test-id"
//                cssAttributeValue = "street-name-house-number"
//            }
//            // not strictly necessary
//            // container = mutableListOf()
//            extractor {
//                text()
//            }
//        }
//        filter {
//        }
//        // etc
//    }
//
//    val buyHouseDocuments = scraper
//        .traverse()
//        .collect {
//        BuyHouseDocument(ZipCodeHouseNumber(it[1], it[1]), it[2], it[3], it[4], it[5], it[6], it[7])
//    }
//}
fun whatever() {
    var whatever = mutableMapOf<String, String>()
    whatever.get("joe")

}

@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ScraperDSL


data class Traversor(
    val root: Elements,
    val attributeFilterMap: MutableMap<Attribute, Filter> = mutableMapOf(),
    val attributeResultMap: MutableMap<Attribute, Any?> = mutableMapOf()
)

@ScraperDSL
class TraversorBuilder {
    var root: Elements = Elements(1)
    var attributeFilterMap: MutableMap<Attribute, Filter> = mutableMapOf()
    var attributeResultMap: MutableMap<Attribute, Any?> = mutableMapOf()
    fun build() = Traversor(root, attributeFilterMap, attributeResultMap)
}

data class Filter(
    val attribute: Attribute,
    val onSuccess: Element.() -> Any,
    val onFailure: () -> Any
)

@ScraperDSL
class FilterBuilder {
    var attribute = Attribute("bla", "bla")
    var onSuccess: Element.() -> Any = {}
    var onFailure: () -> Any = { "None" }
    fun build() = Filter(attribute, onSuccess, onFailure)

}

@ScraperDSL
class AttributeBuilder {
    var cssAttributeKey = ""
    var cssAttributeValue = ""
    var parents: Attributes? = null
    fun build() = Attribute(cssAttributeKey, cssAttributeValue, parents)
}

@ScraperDSL
inline fun TraversorBuilder.filter(block: FilterBuilder.() -> Unit) {
    FilterBuilder().apply(block).build().also {

        if (!attributeFilterMap.containsKey(it.attribute)) {
            this.attributeFilterMap[it.attribute] = it
        }
        attributeResultMap[it.attribute] = null
    }
}

@ScraperDSL
inline fun FilterBuilder.attribute(block: AttributeBuilder.() -> Unit) {
    this.attribute = AttributeBuilder().apply(block).build()
}

@ScraperDSL
fun FilterBuilder.onSuccess(block: Element.() -> Any) {
    this.onSuccess = block
}

@ScraperDSL
fun FilterBuilder.onFailure(block: () -> Any = { "None" }) {
    this.onFailure = block
}


@ScraperDSL
inline fun <T> Traversor.traverseNode(block: Map<String, Any?>.() -> T): List<T> {
    val extractedObjects = root.mapNotNull { element ->
        NodeTraversor.traverse({ node: Node, _: Int ->
            node.attributes().forEach { attribute ->
                val filterMapContainsAttribute = attributeFilterMap.containsKey(attribute)
                if (filterMapContainsAttribute && node is Element) {
                    attributeResultMap[attribute] = attributeFilterMap[attribute]?.onSuccess?.let { it(node) }
                }
            }
        }, element)
        attributeResultMap.forEach { (key, value) ->
            if (value == null) {
                attributeResultMap[key] = attributeFilterMap[key]?.onFailure?.let { it() }
            }
        }
        //
        val keysMappedToStringsMap = attributeResultMap.toMap().mapKeys { it.key.value }
        attributeResultMap.replaceAll { _, _ -> null } // set back to null so the condition in the foreach ^ will be hit
        return@mapNotNull block(keysMappedToStringsMap)
    }
    return extractedObjects
}

fun <V> Traversor.collect(block: (List<String>) -> V): V {
    val lists = attributeResultMap.values.mapNotNull { bla ->
        bla?.toString()
    }
    return block(lists)
}

@ScraperDSL
inline fun traversor(init: TraversorBuilder.() -> Unit): Traversor {
    return TraversorBuilder().apply(init).build()
}

