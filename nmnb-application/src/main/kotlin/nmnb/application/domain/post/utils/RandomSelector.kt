package nmnb.application.domain.post.utils

import kotlin.random.Random

object RandomSelector {
    fun shuffleIds(ids: List<Long>, seed: Int): List<Long> {
        return ids.shuffled(Random(seed))
    }

    fun extractPageIds(shuffledIds: List<Long>, startIndex: Int, size: Int): List<Long> {
        return shuffledIds.drop(startIndex).take(size)
    }
}
