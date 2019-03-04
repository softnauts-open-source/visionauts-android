package com.softnauts.visionauts.repository

import com.softnauts.visionauts.common.ConnectivityManager
import com.softnauts.visionauts.common.DownloadDataException
import com.softnauts.visionauts.data.NetworkService
import com.softnauts.visionauts.data.dao.BeaconsDao
import com.softnauts.visionauts.data.entity.BeaconEntity
import com.softnauts.visionauts.data.entity.BeaconTextContentEntity
import com.softnauts.visionauts.data.model.BeaconDto
import com.softnauts.visionauts.data.model.BeaconTextContentDto
import com.softnauts.visionauts.data.model.BeaconWithTexts
import com.softnauts.visionauts.data.repository.BeaconRepository
import com.softnauts.visionauts.rule.TrampolineSchedulerRule
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.threeten.bp.Instant
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class BeaconsRepositoryTest {

    @get:Rule
    var trampolineSchedulerRule = TrampolineSchedulerRule()

    @Mock
    lateinit var networkService: NetworkService

    @Mock
    lateinit var connectivityManager: ConnectivityManager

    @Mock
    lateinit var beaconsDao: BeaconsDao

    private lateinit var repository: BeaconRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repository = BeaconRepository(networkService, connectivityManager, beaconsDao)
    }

    @Test
    fun getBeaconsOnlineNotEmptyDataTest() {
        Mockito.`when`(connectivityManager.isNetworkAvailable()).thenReturn(true)
        Mockito.`when`(networkService.getBeacons()).thenReturn(Single.just(notEmptyDtoList))

        val observer = repository.getBeaconsFromApiIfNeeded().test()
        observer.assertComplete()

        Mockito.verify(beaconsDao, times(1)).deleteAll()
        Mockito.verify(beaconsDao, times(1)).deleteAllTexts()
        Mockito.verify(beaconsDao, times(1)).insert(notEmptyEntityList)

        for (i in 0 until notEmptyDtoList.size) {
            Mockito.verify(beaconsDao, times(1)).insertBeaconTexts(listOf(notEmptyEntityTextsList[i]))
        }
    }

    @Test
    fun getBeaconsOnlineEmptyTest() {
        Mockito.`when`(connectivityManager.isNetworkAvailable()).thenReturn(true)
        Mockito.`when`(networkService.getBeacons()).thenReturn(Single.just(emptyDtoList))

        val observer = repository.getBeaconsFromApiIfNeeded().test()
        observer.assertComplete()

        Mockito.verify(beaconsDao, times(1)).deleteAll()
        Mockito.verify(beaconsDao, times(1)).deleteAllTexts()
        Mockito.verify(beaconsDao, times(1)).insert(emptyList())

        Mockito.verify(beaconsDao, never()).insertBeaconTexts(ArgumentMatchers.anyList())
    }

    @Test
    fun getBeaconsOnlineNegativeTest() {
        val connectionException = Exception("connection exception")

        Mockito.`when`(connectivityManager.isNetworkAvailable()).thenReturn(true)
        Mockito.`when`(networkService.getBeacons()).thenReturn(Single.error(connectionException))

        val observer = repository.getBeaconsFromApiIfNeeded().test()
        observer.assertError(connectionException)

        Mockito.verify(beaconsDao, never()).deleteAll()
        Mockito.verify(beaconsDao, never()).deleteAllTexts()
        Mockito.verify(beaconsDao, never()).insert(emptyList())

        Mockito.verify(beaconsDao, never()).insertBeaconTexts(ArgumentMatchers.anyList())
    }

    @Test
    fun getBeaconsOfflineNotEmptyDataTest() {
        Mockito.`when`(connectivityManager.isNetworkAvailable()).thenReturn(false)
        Mockito.`when`(beaconsDao.getBeaconsCount()).thenReturn(Single.just(notEmptyEntityModelList.size))

        val observer = repository.getBeaconsFromApiIfNeeded().test()
        observer.assertComplete()

        Mockito.verify(beaconsDao, never()).deleteAll()
        Mockito.verify(beaconsDao, never()).deleteAllTexts()
        Mockito.verify(beaconsDao, never()).insert(emptyList())

        Mockito.verify(beaconsDao, never()).insertBeaconTexts(ArgumentMatchers.anyList())
    }

    @Test
    fun getBeaconsOfflineEmptyDataTest() {
        Mockito.`when`(connectivityManager.isNetworkAvailable()).thenReturn(false)
        Mockito.`when`(beaconsDao.getBeaconsCount()).thenReturn(Single.just(0))

        val observer = repository.getBeaconsFromApiIfNeeded().test()
        observer.assertError(DownloadDataException::class.java)

        Mockito.verify(beaconsDao, never()).deleteAll()
        Mockito.verify(beaconsDao, never()).deleteAllTexts()
        Mockito.verify(beaconsDao, never()).insert(emptyList())

        Mockito.verify(beaconsDao, never()).insertBeaconTexts(ArgumentMatchers.anyList())
    }

    @Test
    fun getBeaconsByIdentifiersPositiveTest() {
        val entityModel = notEmptyEntityModelList.first()
        val dto = notEmptyDtoList.first()

        Mockito.`when`(
            beaconsDao.getBeaconByIdentifiers(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(Maybe.just(entityModel))


        val observer = repository.getBeaconByIdentifier(
            entityModel.beaconEntity!!.uuid,
            entityModel.beaconEntity!!.minor,
            entityModel.beaconEntity!!.major
        ).test()

        observer.assertValue(dto)
        observer.assertComplete()
    }

    @Test
    fun getBeaconsByIdentifiersNegativeTest() {
        val entityModel = notEmptyEntityModelList.first()

        Mockito.`when`(
            beaconsDao.getBeaconByIdentifiers(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(Maybe.empty())


        val observer = repository.getBeaconByIdentifier(
            entityModel.beaconEntity!!.uuid,
            entityModel.beaconEntity!!.minor,
            entityModel.beaconEntity!!.major
        ).test()

        observer.assertNoValues()
        observer.assertComplete()
    }

    // Test data
    private val emptyDtoList = emptyList<BeaconDto>()
    private val notEmptyEntityModelList = listOf(
        BeaconWithTexts().apply {
            beaconEntity = BeaconEntity(
                1L,
                "uuid1",
                "minor1",
                "major1",
                true,
                Instant.ofEpochMilli(0L),
                Instant.ofEpochMilli(0L)
            )
            textEntities = listOf(
                BeaconTextContentEntity(
                    1L,
                    1L,
                    "en",
                    "englishText1",
                    Instant.ofEpochMilli(0L),
                    Instant.ofEpochMilli(0L)
                )
            )
        }, BeaconWithTexts().apply {
            beaconEntity = BeaconEntity(
                2L,
                "uuid2",
                "minor2",
                "major2",
                true,
                Instant.ofEpochMilli(0L),
                Instant.ofEpochMilli(0L)
            )
            textEntities = listOf(
                BeaconTextContentEntity(
                    2L,
                    2L,
                    "en",
                    "englishText2",
                    Instant.ofEpochMilli(0L),
                    Instant.ofEpochMilli(0L)
                )
            )
        })
    private val notEmptyEntityList = listOf(
        BeaconEntity(
            1L,
            "uuid1",
            "minor1",
            "major1",
            true,
            Instant.ofEpochMilli(0L),
            Instant.ofEpochMilli(0L)
        ), BeaconEntity(
            2L,
            "uuid2",
            "minor2",
            "major2",
            true,
            Instant.ofEpochMilli(0L),
            Instant.ofEpochMilli(0L)
        )
    )
    private val notEmptyEntityTextsList = listOf(
        BeaconTextContentEntity(
            1L,
            1L,
            "en",
            "englishText1",
            Instant.ofEpochMilli(0L),
            Instant.ofEpochMilli(0L)
        ),
        BeaconTextContentEntity(
            2L,
            2L,
            "en",
            "englishText2",
            Instant.ofEpochMilli(0L),
            Instant.ofEpochMilli(0L)
        )
    )
    private val notEmptyDtoList = listOf(
        BeaconDto(
            1L, "uuid1", "minor1", "major1", true, listOf(
                BeaconTextContentDto(1L, "en", "englishText1", Date(0L), Date(0L))
            ), Date(0L), Date(0L)
        ),
        BeaconDto(
            2L, "uuid2", "minor2", "major2", true, listOf(
                BeaconTextContentDto(2L, "en", "englishText2", Date(0L), Date(0L))
            ), Date(0L), Date(0L)
        )
    )
}