package me.devnatan.katan.api.server.fs

interface ServerFileDirectory : ServerFile {

    var contents: List<ServerFile>

}