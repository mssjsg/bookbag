package github.io.mssjsg.bookbag.data.source

import io.reactivex.Flowable

interface BookbagDataSource<Item> {
    fun saveItem(item: Item)

    fun moveItem(id: String, folderId: String?)

    fun updateItem(item: Item)

    fun deleteItems(ids: List<String>)

    fun getItem(id: String): Flowable<Item>

    fun getItems(folderId: String? = null) : Flowable<List<Item>>

    fun getDirtyItems() : Flowable<List<Item>>
}