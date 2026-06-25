package com.windnah.core.data.repository

import android.content.Context
import com.windnah.core.domain.repository.FactRepository
import com.windnah.core.model.FactArticle
import com.windnah.core.model.FactCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val FACTS_ASSET_ROOT = "facts"
private const val FRONTMATTER_DELIMITER = "---"
private const val FACT_BLOCK_HEADING = "## "

class LocalMarkdownFactRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : FactRepository {

    override suspend fun getFacts(): List<FactArticle> =
        withContext(Dispatchers.IO) {
            FactCategory.values()
                .flatMap { category -> loadCategoryFacts(category) }
                .sortedWith(compareBy<FactArticle> { it.category.ordinal }.thenBy { it.title })
        }

    override suspend fun getFact(id: String): FactArticle? =
        getFacts().firstOrNull { it.id == id }

    private fun loadCategoryFacts(category: FactCategory): List<FactArticle> =
        runCatching {
            context.assets.open("$FACTS_ASSET_ROOT/${category.slug}.md")
                .bufferedReader()
                .use { reader -> parseCategoryMarkdown(reader.readText(), category) }
        }.getOrElse { emptyList() }

    private fun parseCategoryMarkdown(
        markdown: String,
        expectedCategory: FactCategory,
    ): List<FactArticle> {
        val lines = markdown.lineSequence().toList()
        val contentStartIndex = lines.findContentStartIndex()
        val category = lines
            .take(contentStartIndex)
            .mapNotNull { line -> line.toKeyValuePair() }
            .toMap()["category"]
            ?.let(FactCategory::fromSlug)
            ?: expectedCategory

        if (category != expectedCategory) return emptyList()

        return lines
            .drop(contentStartIndex)
            .splitFactBlocks()
            .mapNotNull { block -> parseFactBlock(block, category) }
    }

    private fun parseFactBlock(
        blockLines: List<String>,
        category: FactCategory,
    ): FactArticle? {
        val id = blockLines
            .firstOrNull()
            ?.removePrefix(FACT_BLOCK_HEADING)
            ?.trim()
            .orEmpty()
        val contentLines = blockLines
            .drop(1)
            .dropWhile { it.isBlank() }
        val metadataEndIndex = contentLines
            .indexOfFirst { it.isBlank() }
            .takeIf { it >= 0 }
            ?: return null
        val metadata = contentLines
            .take(metadataEndIndex)
            .mapNotNull { line -> line.toKeyValuePair() }
            .toMap()
        val explanation = contentLines
            .drop(metadataEndIndex + 1)
            .joinToString("\n")
            .trim()

        val title = metadata["title"].orEmpty()
        val myth = metadata["myth"].orEmpty()

        if (id.isBlank() || title.isBlank() || myth.isBlank() || explanation.isBlank()) return null

        return FactArticle(
            id = id,
            title = title,
            myth = myth,
            category = category,
            explanation = explanation,
            sources = metadata["sources"].orEmpty().parseSources(),
        )
    }

    private fun List<String>.findContentStartIndex(): Int {
        if (firstOrNull()?.trim() != FRONTMATTER_DELIMITER) return 0

        val closingDelimiterIndex = drop(1)
            .indexOfFirst { it.trim() == FRONTMATTER_DELIMITER }
            .takeIf { it >= 0 }
            ?.plus(1)
            ?: return 0

        return closingDelimiterIndex + 1
    }

    private fun List<String>.splitFactBlocks(): List<List<String>> {
        val blocks = mutableListOf<List<String>>()
        var currentBlock = mutableListOf<String>()

        forEach { line ->
            when {
                line.trim() == FRONTMATTER_DELIMITER -> {
                    if (currentBlock.isNotEmpty()) {
                        blocks += currentBlock.trimBlankEdges()
                        currentBlock = mutableListOf()
                    }
                }

                line.startsWith(FACT_BLOCK_HEADING) && currentBlock.isNotEmpty() -> {
                    blocks += currentBlock.trimBlankEdges()
                    currentBlock = mutableListOf(line)
                }

                line.startsWith(FACT_BLOCK_HEADING) -> {
                    currentBlock += line
                }

                currentBlock.isNotEmpty() -> {
                    currentBlock += line
                }
            }
        }

        if (currentBlock.isNotEmpty()) {
            blocks += currentBlock.trimBlankEdges()
        }

        return blocks.filter { block -> block.firstOrNull()?.startsWith(FACT_BLOCK_HEADING) == true }
    }

    private fun List<String>.trimBlankEdges(): List<String> =
        dropWhile { it.isBlank() }
            .dropLastWhile { it.isBlank() }

    private fun String.toKeyValuePair(): Pair<String, String>? {
        val separatorIndex = indexOf(":")
        if (separatorIndex <= 0) return null

        val key = substring(0, separatorIndex).trim()
        val value = substring(separatorIndex + 1).trim().trimSurroundingQuotes()
        return key to value
    }

    private fun String.trimSurroundingQuotes(): String =
        trim()
            .removeSurrounding("\"")
            .removeSurrounding("'")

    private fun String.parseSources(): List<String> =
        split("|", ";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
