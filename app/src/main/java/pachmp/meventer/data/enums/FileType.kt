package pachmp.meventer.data.enums

enum class FileType(val extensions: List<String> = emptyList()) {
    IMAGE(listOf("png", "jpg", "webp")),
    FILE();

    companion object {
        fun getFileType(filePath: String): FileType {
            val extension = filePath.substringAfterLast('.')

            values().forEach {
                if (extension in it.extensions) {
                    return it
                }
            }

            return FILE
        }
    }
}