package com.lendsumapp.lendsum

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.data.persistence.BundleDao
import com.lendsumapp.lendsum.util.TestUtil
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class DatabaseBundleTest {
    
    private lateinit var bundleDao: BundleDao
    private lateinit var db: LendsumDatabase

    @Before
    fun createDb(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, LendsumDatabase::class.java).build()
        bundleDao = db.getBundleDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    //write and read bundle
    @Test
    @Throws(Exception::class)
    fun writeBundleAndRead() = runBlocking{

        val bundleWrite = TestUtil.getFirstTestBundle()

        bundleDao.insertBundle(bundleWrite)

        val bundleRead = bundleDao.findLender("A")

        assertEquals("A", bundleRead[0].lenderName)
        bundleDao.deleteAllBundles()
    }

    //write, read, and delete bundle
    @Test
    @Throws(Exception::class)
    fun writeReadAndDeleteBundle() = runBlocking{

        val bundleWrite = TestUtil.getFirstTestBundle()
        bundleDao.insertBundle(bundleWrite)
        assertEquals(bundleWrite.lenderName, bundleDao.findLender("A")[0].lenderName)

        bundleDao.deleteBundle(bundleWrite)

        val bundleList = bundleDao.findLender("A")
        assertEquals(0, bundleList.size)
    }

    //write multiple entries and delete all bundles
    @Test
    @Throws(Exception::class)
    fun writeAndDeleteAllBundles() = runBlocking{

        val bundle1 = TestUtil.getFirstTestBundle()
        val bundle2 = TestUtil.getSecondTestBundle()

        bundleDao.insertBundle(bundle1)
        bundleDao.insertBundle(bundle2)

        val bundleList = bundleDao.getAllLendBundles()
        assertEquals(2, bundleList.size)

        bundleDao.deleteAllBundles()
        val emptyList = bundleDao.getAllLendBundles()
        assertEquals(0, emptyList.size)

    }




}