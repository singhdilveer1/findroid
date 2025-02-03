package dev.jdtech.jellyfin.settings.presentation.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.jdtech.jellyfin.settings.domain.models.Preference as PreferenceBackend

data class PreferenceLongInput(
    @StringRes override val nameStringResource: Int,
    @StringRes override val descriptionStringRes: Int? = null,
    @DrawableRes override val iconDrawableId: Int? = null,
    override val enabled: Boolean = true,
    override val dependencies: List<PreferenceBackend<Boolean>> = emptyList(),
    val onClick: (Preference) -> Unit = {},
    val backendPreference: PreferenceBackend<Long>,
    val prefix: String? = null,
    val suffix: String? = null,
    val value: Long = -1L,
) : Preference
