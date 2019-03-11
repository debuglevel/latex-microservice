package de.debuglevel.latex.domain.latex

import de.debuglevel.latex.rest.latex.FileDTO
import de.debuglevel.latex.rest.latex.LatexRequestDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentStorageTests {
    @ParameterizedTest
    @MethodSource("invalidFilenameProvider")
    fun `prevents directory traversal attack`(testData: FilenameTestData) {
        // Arrange
        val storedFileTransferDTO = DocumentStorage.add(
            LatexRequestDTO(
                arrayOf(),
                arrayOf(FileDTO(testData.value, "Zm9vYmFy"))
            )
        )

        // Act & Assert
        assertThrows<DocumentStorage.InvalidPathException> { DocumentStorage.get(storedFileTransferDTO.uuid) }
    }

    @ParameterizedTest
    @MethodSource("validFilenameProvider")
    fun `allow valid paths`(testData: FilenameTestData) {
        // Arrange
        val storedFileTransferDTO = DocumentStorage.add(
            LatexRequestDTO(
                arrayOf(),
                arrayOf(FileDTO(testData.value, "Zm9vYmFy"))
            )
        )

        // Act
        val x = DocumentStorage.get(storedFileTransferDTO.uuid)

        // Assert
        assertThat(x).isNotNull
    }

//    @ParameterizedTest
//    @MethodSource("markdownToHtmlProvider")
//    fun `convert to HTML`(testData: LatexTestData) {
//        // Arrange
//        val outputStream = ByteArrayOutputStream()
//
//        // Act
//        HtmlConverter.convert(testData.value, outputStream)
//        val html = String(outputStream.toByteArray())
//
//        //Assert
//        assertThat(html).isEqualTo(testData.expected)
//    }
//
//    fun markdownToHtmlProvider() = Stream.of(
//        LatexTestData(value = "This is *Sparta*", expected = "<p>This is <em>Sparta</em></p>\n")
//    )
//
//    @ParameterizedTest
//    @MethodSource("latexToPdfProvider")
//    fun `convert to Plaintext`(testData: LatexTestData) {
//        // Arrange
//        val outputStream = ByteArrayOutputStream()
//
//        // Act
//        PlaintextConverter.convert(testData.value, outputStream)
//        val plaintext = String(outputStream.toByteArray())
//
//        //Assert
//        assertThat(plaintext).isEqualTo(testData.expected)
//    }

    fun validFilenameProvider() = Stream.of(
        FilenameTestData(value = "test.tex"),
        FilenameTestData(value = "test/test.tex"),
        FilenameTestData(value = "./test.tex"),
        FilenameTestData(value = "test/./test.tex"),
        FilenameTestData(value = "test/../test.tex")
    )

    fun invalidFilenameProvider() = Stream.of(
        FilenameTestData(value = "/test.tex"),
        FilenameTestData(value = "/tmp/test.tex"),
        FilenameTestData(value = "../test.tex"),
        FilenameTestData(value = "temp/../../test.tex")
    )

    data class FilenameTestData(
        val value: String
    )
}