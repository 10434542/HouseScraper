package org.ray.housewebscraper.util


import org.jsoup.nodes.Attribute
import org.jsoup.nodes.Attributes
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import org.jsoup.select.NodeTraversor
import java.util.*

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

@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
annotation class ScraperDSL


data class Traversor(
    val root: Elements,
    val attributeFilterMap: MutableMap<Attribute, Filter> = mutableMapOf(),
    val containersFilterMap: MutableMap<Filter, MutableCollection<Any>> = mutableMapOf()
)

@ScraperDSL
class TraversorBuilder {
    var root: Elements = Elements(1)
    var attributeFilterMap: MutableMap<Attribute, Filter> = mutableMapOf()
    var containersFilterMap: MutableMap<Filter, MutableCollection<Any>> = mutableMapOf()
    fun build() = Traversor(root, attributeFilterMap, containersFilterMap)
}

data class Filter(
    val container: MutableCollection<Any>,
    val attribute: Attribute,
    val onSuccess: Element.() -> Any,
    val onFailure: () -> Any
)

@ScraperDSL
class FilterBuilder {
    var attribute = Attribute("bla", "bla")
    var container = mutableListOf<Any>()
    var onSuccess: Element.() -> Any = {}
    var onFailure: () -> Any = {"None"}
    fun build() = Filter(container, attribute, onSuccess, onFailure)

}

@ScraperDSL
class AttributeBuilder {
    var cssAttributeKey = ""
    var cssAttributeValue = ""
    var parents: Attributes? = null
    fun build() = Attribute(cssAttributeKey, cssAttributeValue, parents)
}

inline fun TraversorBuilder.filter(block: FilterBuilder.() -> Unit) {
    FilterBuilder().apply(block).build().also {

        if (!attributeFilterMap.containsKey(it.attribute)) {
            this.attributeFilterMap[it.attribute] = it
        }
        if (!containersFilterMap.containsKey(it)) {
            this.containersFilterMap[it] = it.container
        }
    }
}

inline fun FilterBuilder.attribute(block: AttributeBuilder.() -> Unit) {
    this.attribute = AttributeBuilder().apply(block).build()
}

fun FilterBuilder.onSuccess(block: Element.() -> Any) {
    this.onSuccess = block
}

fun FilterBuilder.onFailure(block: () -> Any) {
    this.onFailure = block
}


fun Traversor.traverse(): Traversor {
    NodeTraversor.traverse({ node: Node, _: Int ->
        node.attributes().forEach {
            val contains = attributeFilterMap.containsKey(it)
            if (contains && node is Element) {
                attributeFilterMap[it]
                    ?.let { it1 -> containersFilterMap[attributeFilterMap[it]]?.add(it1.onSuccess(node)) }

            }
        }
    }, root)
    // TODO: fill empty containers with default values?
    val maxSize = containersFilterMap.values.maxOfOrNull { it.size }
    val whatever = containersFilterMap.keys.filter {
        containersFilterMap[it]!!.isEmpty()
    }
    // above filter gives nullpointer despite the key being there? what?
    whatever.forEach { containersFilterMap[it]!!.addAll(Collections.nCopies(maxSize!!, it.onFailure())) }

    return this
}

fun <V> Traversor.collect(block: (List<String>) -> V): List<V> {
    val lists = containersFilterMap.values.map { bla ->
        bla.map {
            it.toString()
        }
    }.toTypedArray()
    return zip(*lists, transform = { block(it) })
}


inline fun traversor(init: TraversorBuilder.() -> Unit): Traversor {
    val returnValue = TraversorBuilder().apply(init).build()
    return returnValue
//    return TraversorBuilder().apply(init).build()
}

