package com.example.ai

object GeminiResponseParser {

    fun extractTextFromResponse(response: GeminiResponse?, fallback: String): String {
        if (response == null) return fallback

        val candidates = response.candidates
        if (candidates.isNullOrEmpty()) return fallback

        val firstCandidate = candidates.firstOrNull() ?: return fallback
        val text = firstCandidate.content?.parts?.firstOrNull()?.text

        return if (!text.isNullOrBlank() && text.length >= 10) {
            cleanMarkdown(text.trim())
        } else {
            fallback
        }
    }

    private fun cleanMarkdown(raw: String): String {
        // Strip markdown code fences if wrapped
        return raw.removePrefix("```markdown")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }
}
