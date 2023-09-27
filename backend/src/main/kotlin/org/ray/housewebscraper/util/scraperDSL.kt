package org.ray.housewebscraper.util


import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import org.jsoup.select.NodeTraversor
import org.ray.housewebscraper.persistence.BuyHouseDocument

/*
    TODO: nodetraverser object instantiation with possible filters of which the results can be appended to lists or something

 */
fun main() {

    val scraper = traversor {
        root = Elements(1)
        filter {
            cssAttribute = "whatever"
            container = mutableListOf()
            extractor = {
                text()
            }
        }
        filter {

        }
        // etc
    }

    val buyHouseDocuments = scraper.collect {
        BuyHouseDocument(it[1], it[1], it[2], it[3], it[4], it[5], it[6], it[7])
    }
}

data class Traversor(
    val root: Elements,
    val attributeFilterMap: MutableMap<String, Filter> = mutableMapOf(),
    val containersFilterMap: MutableMap<Filter, MutableCollection<Any>> = mutableMapOf()
)

class TraversorBuilder {
    var root: Elements = Elements(1)
    var attributeFilterMap: MutableMap<String, Filter> = mutableMapOf()
    var containersFilterMap: MutableMap<Filter, MutableCollection<Any>> = mutableMapOf()
    fun build() = Traversor(root, attributeFilterMap, containersFilterMap)
}

data class Filter(
    val container: MutableCollection<Any>,
    val cssAttribute: String,
    val extractor: Element.() -> Any
)

class FilterBuilder {
    var cssAttribute = ""
    var container = mutableListOf<Any>()
    var extractor: Element.() -> Any = {}
    fun build() = Filter(container, cssAttribute, extractor)

}

fun TraversorBuilder.filter(block: FilterBuilder.() -> Unit) {
    FilterBuilder().apply(block).build().also {

        if (!attributeFilterMap.containsKey(it.cssAttribute)) {
            this.attributeFilterMap[it.cssAttribute] = it
        }
        if (!containersFilterMap.containsKey(it)) {
            this.containersFilterMap[it] = it.container
        }
    }
}


private fun Traversor.traverse() {
    NodeTraversor.traverse({ node: Node, _: Int ->
        node.attributes().forEach {
            val contains = attributeFilterMap.containsKey(it.toString())
            if (contains) {
                attributeFilterMap[it.toString()]
                    ?.let { it1 -> containersFilterMap[attributeFilterMap[it.toString()]]?.add(it1.extractor) }

            }
        }
    }, root)
}

fun <T, V> Traversor.collect(block: (List<T>) -> V): List<V> {
    val lists = containersFilterMap.values.map { it.toList() }.toTypedArray()
    return zip(*lists, transform = { block(it) })
}


fun traversor(init: TraversorBuilder.() -> Unit): Traversor {
    return TraversorBuilder().apply(init).build()
}

