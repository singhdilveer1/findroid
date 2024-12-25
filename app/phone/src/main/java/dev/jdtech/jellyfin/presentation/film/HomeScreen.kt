package dev.jdtech.jellyfin.presentation.film

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jdtech.jellyfin.core.presentation.dummy.dummyHomeSection
import dev.jdtech.jellyfin.core.presentation.dummy.dummyHomeView
import dev.jdtech.jellyfin.film.presentation.home.HomeAction
import dev.jdtech.jellyfin.film.presentation.home.HomeState
import dev.jdtech.jellyfin.film.presentation.home.HomeViewModel
import dev.jdtech.jellyfin.presentation.components.ErrorDialog
import dev.jdtech.jellyfin.presentation.film.components.Direction
import dev.jdtech.jellyfin.presentation.film.components.ErrorCard
import dev.jdtech.jellyfin.presentation.film.components.ItemCard
import dev.jdtech.jellyfin.presentation.theme.FindroidTheme
import dev.jdtech.jellyfin.presentation.theme.spacings
import dev.jdtech.jellyfin.core.R as CoreR
import dev.jdtech.jellyfin.film.R as FilmR

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.loadData()
    }

    HomeScreenLayout(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenLayout(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val startPadding = with(density) { WindowInsets.safeDrawing.getLeft(this, layoutDirection).toDp() + MaterialTheme.spacings.default }
    val endPadding = with(density) { WindowInsets.safeDrawing.getRight(this, layoutDirection).toDp() + MaterialTheme.spacings.default }

    val itemsPadding = PaddingValues(
        start = startPadding,
        end = endPadding,
    )

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val searchBarPaddingStart by animateDpAsState(
        targetValue = if (expanded) 0.dp else startPadding,
        label = "search_bar_padding_start",
    )

    val searchBarPaddingEnd by animateDpAsState(
        targetValue = if (expanded) 0.dp else endPadding,
        label = "search_bar_padding_end",
    )

    val contentTopPadding by animateDpAsState(
        targetValue = if (state.error != null) {
            with(density) { WindowInsets.safeDrawing.getTop(this).toDp() + 136.dp }
        } else {
            with(density) { WindowInsets.safeDrawing.getTop(this).toDp() + 80.dp }
        },
        label = "content_padding",
    )

    var showErrorDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true },
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { expanded = true },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text(stringResource(FilmR.string.search_placeholder)) },
                    leadingIcon = {
                        AnimatedContent(
                            targetState = expanded,
                            label = "search_to_back",
                        ) { targetExpanded ->
                            if (targetExpanded) {
                                IconButton(
                                    onClick = {
                                        expanded = false
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(CoreR.drawable.ic_arrow_left),
                                        contentDescription = null,
                                    )
                                }
                            } else {
                                Icon(
                                    painter = painterResource(CoreR.drawable.ic_search),
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                    trailingIcon = {
                        AnimatedContent(
                            targetState = expanded,
                            label = "search_to_back",
                        ) { targetExpanded ->
                            if (targetExpanded) {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(CoreR.drawable.ic_x),
                                        contentDescription = null,
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {},
                                ) {
                                    Icon(
                                        painter = painterResource(CoreR.drawable.ic_user),
                                        contentDescription = null,
                                    )
                                }
                            }
                        }
                    },
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = searchBarPaddingStart,
                    end = searchBarPaddingEnd,
                )
                .semantics { traversalIndex = 0f },
        ) { }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .semantics { traversalIndex = 1f },
            contentPadding = PaddingValues(
                top = contentTopPadding,
                bottom = with(density) { WindowInsets.safeDrawing.getBottom(this).toDp() + MaterialTheme.spacings.default },
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacings.small),
        ) {
            items(state.sections, key = { it.id }) { section ->
                Column(
                    modifier = Modifier.animateItem(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .padding(itemsPadding),
                    ) {
                        Text(
                            text = section.homeSection.name.asString(),
                            modifier = Modifier.align(Alignment.CenterStart),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacings.extraSmall))
                    LazyRow(
                        contentPadding = itemsPadding,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacings.default),
                    ) {
                        items(section.homeSection.items, key = { it.id }) { item ->
                            ItemCard(
                                item = item,
                                direction = Direction.HORIZONTAL,
                                onClick = {},
                            )
                        }
                    }
                }
            }
            items(state.views, key = { it.id }) { view ->
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .padding(itemsPadding),
                    ) {
                        Text(
                            text = stringResource(FilmR.string.latest_library, view.view.name),
                            modifier = Modifier.align(Alignment.CenterStart),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        TextButton(
                            onClick = {},
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            Text(stringResource(CoreR.string.view_all))
                        }
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacings.extraSmall))
                    LazyRow(
                        contentPadding = itemsPadding,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacings.default),
                    ) {
                        items(view.view.items, key = { it.id }) { item ->
                            ItemCard(
                                item = item,
                                direction = Direction.VERTICAL,
                                onClick = {},
                            )
                        }
                    }
                }
            }
        }
        if (state.error != null) {
            ErrorCard(
                onShowStacktrace = {
                    showErrorDialog = true
                },
                onRetryClick = {
                    onAction(HomeAction.OnRetryClick)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = startPadding,
                        top = with(density) { WindowInsets.safeDrawing.getTop(this).toDp() + 80.dp },
                        end = endPadding,
                    ),
            )
            if (showErrorDialog) {
                ErrorDialog(
                    exception = state.error!!,
                    onDismissRequest = { showErrorDialog = false },
                )
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun HomeScreenLayoutPreview() {
    FindroidTheme {
        HomeScreenLayout(
            state = HomeState(
                sections = listOf(dummyHomeSection),
                views = listOf(dummyHomeView),
                error = Exception("Failed to load data"),
            ),
            onAction = {},
        )
    }
}
