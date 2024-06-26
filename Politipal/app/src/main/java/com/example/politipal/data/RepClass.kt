package com.example.politipal.data

data class RepClass (
    val id: Long,
    val address: String,
    val ballotpediaID: String,
    val bioguideID: String,
    val birthday: Int,
    val cspanID: Int,
    val district: String,
    val facebook: String,
    val fecIDS: String,
    val firstName: String,
    val fullName: String,
    val gender: String,
    val govtrackID: Int,
    val icpsrID: Int,
    val lisID: String,
    val middleName: String,
    val nickanem: String,
    val opensecretsID: String,
    val party: String,
    val phone: Int,
    val rssURL: String,
    val senateClass: Int,
    val state: String,
    val suffix: String,
    val surname: String,
    val thomasID: String,
    val twitter: String,
    val twitterID: Int,
    val type: String,
    val url: String,
    val votesmartID: Int,
    val wikipediaID: String,
    val youtube: String,
    val youtubeID: String,
    var isStarred: Boolean = false
)