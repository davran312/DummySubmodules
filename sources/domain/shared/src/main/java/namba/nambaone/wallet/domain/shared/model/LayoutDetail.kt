package namba.nambaone.wallet.domain.shared.model

import androidx.annotation.IntRange

data class LayoutDetail(
    val title: String,
    val isExpandable: Boolean,
    val isSearchable: Boolean,
    val templateId: String,
    val orientation: Orientation,
    @IntRange(from = 1)
    val columnNumber: Int,
    val reverseLayout: Boolean = false,
    val titleButton: TitleButton?
)