package com.example.contactreader.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contactreader.R
import java.util.*
import kotlin.collections.ArrayList


// The column index for the _ID column
private const val CONTACT_ID_INDEX: Int = 0

// The column index for the CONTACT_KEY column
private const val CONTACT_KEY_INDEX: Int = 1

class MainFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        fun newInstance() = MainFragment()
    }

    val PROJECTION_NUMBERS: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER)

    val PROJECTION_DETAILS = arrayOf(ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

    val phones = mutableMapOf<Long, List<String>>()
    val contacts = arrayListOf<ContactModel>()
    lateinit var contactsList: RecyclerView
    private var adapter: ContactAdapter? = null
    private val RECORD_REQUEST_CODE = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_CONTACTS)
        Log.i("TAG", "setupPermissions: started at ${Calendar.getInstance().timeInMillis}")
        if (permission != PackageManager.PERMISSION_GRANTED) requestPermission() else LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                RECORD_REQUEST_CODE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initializes the loader
        contactsList = view.findViewById(R.id.list)
        contactsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        // Gets a CursorAdapter
        adapter = ContactAdapter()
        // Sets the adapter for the ListView
        contactsList.adapter = adapter
        setupPermissions()
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            0 -> CursorLoader(
                    requireActivity(),
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PROJECTION_NUMBERS,
                    null,
                    null,
                    null
            )
            else -> CursorLoader(
                    requireActivity(),
                    ContactsContract.Contacts.CONTENT_URI,
                    PROJECTION_DETAILS,
                    null,
                    null,
                    null
            )
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        // Put the result Cursor in the adapter for the ListView
        Log.i("TAG", "onLoadFinished")
        when (loader.id) {
            0 -> {

                data?.let { cursor ->
                    while (cursor.isClosed.not() && cursor.moveToNext()) {
                        val contactId = cursor.getLong(0)
                        val phone = cursor.getString(1)
                        var list = arrayListOf<String>()
                        if (phones.containsKey(contactId)) {
                            phones[contactId]?.let { list.addAll(it) }
                        } else {
                            list = ArrayList()
                            phones[contactId] = list
                        }
                        list.add(phone)
                    }
                    cursor.close()
                }
                LoaderManager.getInstance(this@MainFragment)
                        .initLoader(1, null, this);
            }
            1 -> {
                data?.let { cursor ->
                    while (!cursor.isClosed && cursor.moveToNext()) {
                        val id = cursor.getLong(0)
                        val name = cursor.getString(1)
                        val photo = cursor.getString(2)
                        val contactPhones = phones[id]
                        if (contactPhones != null) {
                            for (phone in contactPhones) {
                                addContact(id, name, phone, photo)
                            }
                        }
                    }
                    cursor.close()
                    Log.i("TAG", "setupPermissions: finished at ${Calendar.getInstance().timeInMillis}")
                    adapter?.submitList(contacts)
                }
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Delete the reference to the existing Cursor
        adapter?.submitList(null)
    }
    private fun addContact(id: Long, name: String?, phone: String?, photo: String?) {
        contacts.add(ContactModel(id, name, phone, photo))
    }
}