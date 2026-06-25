package com.windnah.core.model

enum class FactCategory(
    val slug: String,
    val label: String,
) {
    NatureAndEnvironment("natur-und-umwelt", "Natur & Umwelt"),
    HumanAndHealth("mensch-und-gesundheit", "Mensch & Gesundheit"),
    EnergyAndTechnology("energie-und-technik", "Energie & Technik"),
    EconomyAndSociety("wirtschaft-und-gesellschaft", "Wirtschaft & Gesellschaft"),
    ;

    companion object {
        fun fromSlug(slug: String): FactCategory? =
            values().firstOrNull { it.slug == slug }
    }
}
