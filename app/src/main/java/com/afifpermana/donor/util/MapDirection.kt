package com.afifpermana.donor.util

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory


@SuppressLint("NewApi")
class MapDirection {
    fun getDocument(start: LatLng, end: LatLng, mode: String?): Document? {
        var urlConnection: HttpURLConnection? = null
        var `in`: InputStream? = null

        try {
            val urlString = ("http://maps.googleapis.com/maps/api/directions/xml?"
                    + "origin=" + start.latitude + "," + start.longitude
                    + "&destination=" + end.latitude + "," + end.longitude
                    + "&sensor=false&units=metric&mode=driving")
            val url = URL(urlString)
            urlConnection = url.openConnection() as HttpURLConnection
            `in` = urlConnection.inputStream

            val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val document = builder.parse(`in`)
            Log.e("direction", document.toString())
            return document
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("direction", e.toString())
        } finally {
            // Pastikan untuk menutup stream dan koneksi setelah selesai
            `in`?.close()
            urlConnection?.disconnect()
        }
        return null
    }


    fun getDurationText(doc: Document): String {
        val nl1 = doc.getElementsByTagName("duration")
        val node1 = nl1.item(nl1.length - 1)
        val nl2 = node1.childNodes
        val node2 = nl2.item(getNodeIndex(nl2, "text"))
        Log.i("DurationText", node2.textContent)
        return node2.textContent
    }

    fun getDurationValue(doc: Document): Int {
        val nl1 = doc.getElementsByTagName("duration")
        val node1 = nl1.item(nl1.length - 1)
        val nl2 = node1.childNodes
        val node2 = nl2.item(getNodeIndex(nl2, "value"))
        Log.i("DurationValue", node2.textContent)
        return node2.textContent.toInt()
    }

    fun getDistanceText(doc: Document): String {
        val nl1 = doc.getElementsByTagName("distance")
        val node1 = nl1.item(nl1.length - 1)
        val nl2 = node1.childNodes
        val node2 = nl2.item(getNodeIndex(nl2, "text"))
        Log.i("DistanceText", node2.textContent)
        return node2.textContent
    }

    fun getDistanceValue(doc: Document): Int {
        val nl1 = doc.getElementsByTagName("distance")
        val node1 = nl1.item(nl1.length - 1)
        val nl2 = node1.childNodes
        val node2 = nl2.item(getNodeIndex(nl2, "value"))
        Log.i("DistanceValue", node2.textContent)
        return node2.textContent.toInt()
    }

    fun getStartAddress(doc: Document): String {
        val nl1 = doc.getElementsByTagName("start_address")
        val node1 = nl1.item(0)
        Log.i("StartAddress", node1.textContent)
        return node1.textContent
    }

    fun getEndAddress(doc: Document): String {
        val nl1 = doc.getElementsByTagName("end_address")
        val node1 = nl1.item(0)
        Log.i("StartAddress", node1.textContent)
        return node1.textContent
    }

    fun getCopyRights(doc: Document): String {
        val nl1 = doc.getElementsByTagName("copyrights")
        val node1 = nl1.item(0)
        Log.i("CopyRights", node1.textContent)
        return node1.textContent
    }

    fun getDirection(doc: Document): ArrayList<LatLng> {
        val nl1: NodeList
        var nl2: NodeList
        var nl3: NodeList
        val listGeopoints = ArrayList<LatLng>()
        nl1 = doc.getElementsByTagName("step")
        if (nl1.length > 0) {
            for (i in 0 until nl1.length) {
                val node1 = nl1.item(i)
                nl2 = node1.childNodes
                var locationNode = nl2.item(getNodeIndex(nl2, "start_location"))
                nl3 = locationNode.childNodes
                var latNode = nl3.item(getNodeIndex(nl3, "lat"))
                var lat = latNode.textContent.toDouble()
                var lngNode = nl3.item(getNodeIndex(nl3, "lng"))
                var lng = lngNode.textContent.toDouble()
                listGeopoints.add(LatLng(lat, lng))
                locationNode = nl2.item(getNodeIndex(nl2, "polyline"))
                nl3 = locationNode.childNodes
                latNode = nl3.item(getNodeIndex(nl3, "points"))
                val arr = decodePoly(latNode.textContent)
                for (j in arr.indices) {
                    listGeopoints.add(LatLng(arr[j].latitude, arr[j].longitude))
                }
                locationNode = nl2.item(getNodeIndex(nl2, "end_location"))
                nl3 = locationNode.childNodes
                latNode = nl3.item(getNodeIndex(nl3, "lat"))
                lat = latNode.textContent.toDouble()
                lngNode = nl3.item(getNodeIndex(nl3, "lng"))
                lng = lngNode.textContent.toDouble()
                listGeopoints.add(LatLng(lat, lng))
            }
        }
        return listGeopoints
    }

    private fun getNodeIndex(nl: NodeList, nodename: String): Int {
        for (i in 0 until nl.length) {
            if (nl.item(i).nodeName == nodename) return i
        }
        return -1
    }

    private fun decodePoly(encoded: String): ArrayList<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val position = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(position)
        }
        return poly
    }

    companion object {
        const val MODE_DRIVING = "driving"
        const val MODE_WALKING = "walking"
    }
}