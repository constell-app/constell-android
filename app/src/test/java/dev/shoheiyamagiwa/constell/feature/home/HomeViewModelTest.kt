package dev.shoheiyamagiwa.constell.feature.home

import dev.shoheiyamagiwa.constell.data.repository.UserPreferencesRepository
import dev.shoheiyamagiwa.constell.feature.home.data.ArticleRepository
import dev.shoheiyamagiwa.constell.feature.home.model.Article
import dev.shoheiyamagiwa.constell.feature.home.model.ArticleConnection
import dev.shoheiyamagiwa.constell.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
public class HomeViewModelTest {
    @get:Rule
    public val mainDispatcherRule = MainDispatcherRule()

    private val repository: ArticleRepository = mockk()
    private val userPreferencesRepository: UserPreferencesRepository = mockk()
    private lateinit var viewModel: HomeViewModel

    @Before
    public fun setup() {
        every { userPreferencesRepository.userId } returns flowOf(value = "u1")
        viewModel = HomeViewModel(
            articleRepository = repository,
            userPreferencesRepository = userPreferencesRepository
        )
    }

    @Test
    public fun `initial state is Loading`() {
        assertEquals(HomeScreenState.Loading, viewModel.screenState.value)
    }

    @Test
    public fun `initializeScreen sets Default with null mainArticleNode when no articles`() =
        runTest(context = mainDispatcherRule.testDispatcher) {
            coEvery { repository.getArticles(userId = any()) } returns emptyList()
            coEvery { repository.getArticleConnections(userId = any()) } returns emptyList()

            viewModel.initializeScreen()
            runCurrent()

            val state = viewModel.screenState.value
            assertTrue(state is HomeScreenState.Default)
            val defaultState = state as HomeScreenState.Default
            assertEquals(null, defaultState.mainArticleNode)
        }

    @Test
    public fun `initializeScreen sets Default with mainArticleNode and similar articles`() =
        runTest(context = mainDispatcherRule.testDispatcher) {
            val articles = listOf(
                Article(
                    id = "1",
                    userId = "u1",
                    url = "url1",
                    title = "T1",
                    tags = listOf("tag1"),
                    summary = "S1"
                ),
                Article(
                    id = "2",
                    userId = "u1",
                    url = "url2",
                    title = "T2",
                    tags = listOf("tag2"),
                    summary = "S2"
                ),
                Article(
                    id = "3",
                    userId = "u1",
                    url = "url3",
                    title = "T3",
                    tags = listOf("tag3"),
                    summary = "S3"
                )
            )
            val connections = listOf(
                ArticleConnection(
                    id = "c1",
                    userId = "u1",
                    sourceArticleId = "1",
                    targetArticleId = "2",
                    similarityScore = 0.9
                ),
                ArticleConnection(
                    id = "c2",
                    userId = "u1",
                    sourceArticleId = "3",
                    targetArticleId = "1",
                    similarityScore = 0.7
                )
            )

            coEvery { repository.getArticles(userId = "u1") } returns articles
            coEvery { repository.getArticleConnections(userId = "u1") } returns connections

            viewModel.initializeScreen()
            runCurrent()

            val state = viewModel.screenState.value
            assertTrue(state is HomeScreenState.Default)

            val defaultState = state as HomeScreenState.Default
            assertEquals("1", defaultState.mainArticleNode?.id)
            assertEquals(2, defaultState.mainArticleNode?.similarArticles?.size)

            val similarIds = defaultState.mainArticleNode?.similarArticles?.map { it.id }
            assertTrue(similarIds?.contains("2") == true)
            assertTrue(similarIds?.contains("3") == true)
        }

    @Test
    public fun `selectArticleById updates mainArticleNode and shows details`() =
        runTest(context = mainDispatcherRule.testDispatcher) {
            val articles = listOf(
                Article(
                    id = "1",
                    userId = "u1",
                    url = "url1",
                    title = "T1",
                    tags = listOf("tag1"),
                    summary = "S1"
                ),
                Article(
                    id = "2",
                    userId = "u1",
                    url = "url2",
                    title = "T2",
                    tags = listOf("tag2"),
                    summary = "S2"
                )
            )
            coEvery { repository.getArticles(userId = "u1") } returns articles
            coEvery { repository.getArticleConnections(userId = "u1") } returns emptyList()

            viewModel.initializeScreen()
            runCurrent()
            viewModel.selectArticleById(articleId = "2")

            val state = viewModel.screenState.value
            assertTrue(state is HomeScreenState.Default)
            val defaultState = state as HomeScreenState.Default
            assertEquals("2", defaultState.mainArticleNode?.id)
            assertTrue(defaultState.showArticleDetails)
        }

    @Test
    public fun `setShowArticleDetails updates showArticleDetails`() =
        runTest(context = mainDispatcherRule.testDispatcher) {
            coEvery { repository.getArticles(userId = "u1") } returns listOf(
                Article(
                    id = "1",
                    userId = "u1",
                    url = "url1",
                    title = "T1",
                    tags = listOf("tag1"),
                    summary = "S1"
                )
            )
            coEvery { repository.getArticleConnections(userId = "u1") } returns emptyList()

            viewModel.initializeScreen()
            runCurrent()
            viewModel.setShowArticleDetails(true)

            var state = viewModel.screenState.value as HomeScreenState.Default
            assertTrue(state.showArticleDetails)

            viewModel.setShowArticleDetails(false)
            state = viewModel.screenState.value as HomeScreenState.Default
            assertTrue(!state.showArticleDetails)
        }

    @Test
    public fun `initializeScreen sets Error state when repository throws exception`() =
        runTest(context = mainDispatcherRule.testDispatcher) {
            val exception = Exception("Network error")
            coEvery { repository.getArticles(userId = any()) } throws exception

            viewModel.initializeScreen()
            runCurrent()

            val state = viewModel.screenState.value
            assertTrue(state is HomeScreenState.Error)
            assertEquals(exception, (state as HomeScreenState.Error).exception)
        }
}
