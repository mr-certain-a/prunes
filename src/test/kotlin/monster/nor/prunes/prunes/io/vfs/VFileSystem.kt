package monster.nor.prunes.prunes.io.vfs

import java.nio.file.*
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.file.spi.FileSystemProvider

object VFileSystem: FileSystem() {
    override fun close() {
        TODO("Not yet implemented")
    }

    override fun provider(): FileSystemProvider = VFileSystemProvider

    override fun isOpen(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSeparator(): String = "/"

    override fun getRootDirectories(): MutableIterable<Path> {
        TODO("Not yet implemented")
    }

    override fun getFileStores(): MutableIterable<FileStore> {
        TODO("Not yet implemented")
    }

    override fun supportedFileAttributeViews(): MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun getPath(first: String, vararg more: String?): Path {
        TODO("Not yet implemented")
    }

    override fun getPathMatcher(syntaxAndPattern: String?): PathMatcher {
        TODO("Not yet implemented")
    }

    override fun getUserPrincipalLookupService(): UserPrincipalLookupService {
        TODO("Not yet implemented")
    }

    override fun newWatchService(): WatchService {
        TODO("Not yet implemented")
    }
}