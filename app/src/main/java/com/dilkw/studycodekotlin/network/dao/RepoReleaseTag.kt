package com.dilkw.studycodekotlin.network.dao

data class RepoReleaseTag(
    val commit: Commit,
    val name: String,
    val node_id: String,
    val tarball_url: String,
    val zipball_url: String
)