package com.lendsumapp.lendsum

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lendsumapp.lendsum.data.model.Bundle
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.data.persistence.BundleDao
import com.lendsumapp.lendsum.util.TestUtil
import dagger.hilt.android.AndroidEntryPoint
import junit.framework.Assert.*
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class BundleDatabaseTest {

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

        val bundleWrite = TestUtil.getFirstLendTestBundle()

        bundleDao.insertBundle(bundleWrite)

        val bundleRead = bundleDao.findLender("A")

        assertEquals("A", bundleRead[0].lenderName)
    }

    //write, read, and delete bundle
    @Test
    @Throws(Exception::class)
    fun writeReadAndDeleteBundle() = runBlocking{

        val bundleWrite = TestUtil.getFirstLendTestBundle()
        bundleDao.insertBundle(bundleWrite)
        assertEquals(bundleWrite.lenderName, bundleDao.findLender("A")[0].lenderName)

        bundleDao.deleteBundle(bundleWrite)

        val bundleList = bundleDao.findLender("A")
        assertEquals(0, bundleList.size)
    }

    //write, read, update, read, delete
    @Test
    @Throws(Exception::class)
    fun writeReadUpdateReadDelete() = runBlocking {

        //write
        val bundleWrite = TestUtil.getFirstLendTestBundle()

        //read
        val key: Long = bundleDao.insertBundle(bundleWrite)
        assertEquals(1, bundleDao.getLendBundles().size)
        assertNotNull(key)

        //update
        val value: Long = 9999
        val readBundle = bundleDao.getBundleById(key)
        readBundle.returnDate = value
        bundleDao.updateBundle(readBundle)

        //read
        val dbReturnDate = bundleDao.getBundleById(key)
        val updatedReturnDate = dbReturnDate.returnDate

        //Test
        assertEquals(value, updatedReturnDate)

    }

    //write multiple entries and delete all bundles
    @Test
    @Throws(Exception::class)
    fun writeAndDeleteAllBundles() = runBlocking{

        val bundleList = listOf<Bundle>(TestUtil.getFirstLendTestBundle(),
            TestUtil.getSecondLendTestBundle(),
            TestUtil.getThirdBorrowTestBundle(),
            TestUtil.getFourthBorrowTestBundle())

        //write multiple entries
        bundleList.forEach {
            bundleDao.insertBundle(it)
        }

        //check to see if the size of items in database are what we inserted
        val dbBundleList = bundleDao.getAllBundles()
        assertEquals(4, dbBundleList.size)

        //delete everything and check if db is empty
        bundleDao.deleteAllBundles()
        val emptyList = bundleDao.getAllBundles()
        assertEquals(0, emptyList.size)

    }

    //write multiple entries, read only lending bundles
    @Test
    @Throws(Exception::class)
    fun writeAndReadLendingBundlesOnly() = runBlocking {

        //Create list of bundle objects
        val bundleList : List<Bundle> = listOf(TestUtil.getFirstLendTestBundle(),
            TestUtil.getSecondLendTestBundle(),
            TestUtil.getThirdBorrowTestBundle(),
            TestUtil.getFourthBorrowTestBundle())

        //Insert all items of list into the database
        bundleList.forEach {
            bundleDao.insertBundle(it)
        }

        //Make sure sizes match with local and database list
        val dbBundleList = bundleDao.getAllBundles()
        assertEquals(bundleList.size, dbBundleList.size)

        //retrieve lend bundles from database
        val dbLendList = bundleDao.getLendBundles()
        assertEquals(2, dbLendList.size)

        //check to see if the lender name data matches and is what we expect
        dbLendList.forEach {
            assertTrue(it.lenderName == "A" || it.lenderName == "C")
        }
    }

    //write multiple entries, read only borrow bundles
    @Test
    @Throws(Exception::class)
    fun writeAndReadBorrowBundlesOnly() = runBlocking{

        //Create list of bundle objects
        val bundleList : List<Bundle> = listOf(TestUtil.getFirstLendTestBundle(),
            TestUtil.getSecondLendTestBundle(),
            TestUtil.getThirdBorrowTestBundle(),
            TestUtil.getFourthBorrowTestBundle())

        //Insert all items of list into the database
        bundleList.forEach {
            bundleDao.insertBundle(it)
        }

        //Make sure sizes match with local and database list
        val dbBundleList = bundleDao.getAllBundles()
        assertEquals(bundleList.size, dbBundleList.size)

        //retrieve borrow bundles from database
        val dbBorrowList = bundleDao.getBorrowBundles()
        assertEquals(2, dbBorrowList.size)

        //check to see if the lender name data matches and is what we expect
        dbBorrowList.forEach {
            assertTrue(it.lenderName == "E" || it.lenderName == "G")
        }

    }

}