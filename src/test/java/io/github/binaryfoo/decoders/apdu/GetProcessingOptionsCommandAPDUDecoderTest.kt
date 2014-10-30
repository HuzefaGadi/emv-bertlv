package io.github.binaryfoo.decoders.apdu

import io.github.binaryfoo.DecodedData
import io.github.binaryfoo.EmvTags
import io.github.binaryfoo.QVsdcTags
import io.github.binaryfoo.decoders.DecodeSession
import org.junit.Test

import org.hamcrest.Matchers.hasItem
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import io.github.binaryfoo.AmexTags
import io.github.binaryfoo.tlv.Tag
import io.github.binaryfoo.decoders.annotator
import io.github.binaryfoo.decoders.annotator.backgroundOf

public class GetProcessingOptionsCommandAPDUDecoderTest {

    Test
    public fun testDecodeVisaWithPDOL() {
        val session = DecodeSession()
        session.tagMetaData = QVsdcTags.METADATA
        session.put(EmvTags.PDOL, "9F66049F02069F03069F1A0295055F2A029A039C019F3704")
        val input = "80A8000023832136000000000000001000000000000000003600000000000036120315000008E4C800"
        val decoded = GetProcessingOptionsCommandAPDUDecoder().decode(input, 0, session)
        assertThat(decoded.rawData, `is`("C-APDU: GPO"))
        val children = decoded.children
        val expectedDecodedTTQ = QVsdcTags.METADATA.get(QVsdcTags.TERMINAL_TX_QUALIFIERS).decoder.decode("36000000", 7, DecodeSession())
        assertThat(children.first, `is`(DecodedData(Tag.fromHex("9F66"), "9F66 (TTQ - Terminal transaction qualifiers)", "36000000", 7, 11, expectedDecodedTTQ)))
        assertThat(children.last, `is`(DecodedData(EmvTags.UNPREDICTABLE_NUMBER, "9F37 (unpredictable number)", "0008E4C8", 36, 40, backgroundReading = backgroundOf("A terminal nonce generated nonce for replay attack prevention"))))
    }

    Test
    public fun testDecodeMastercardWithoutPDOL() {
        val session = DecodeSession()
        val input = "80A8000002830000"
        val decoded = GetProcessingOptionsCommandAPDUDecoder().decode(input, 0, session)
        assertThat(decoded.rawData, `is`("C-APDU: GPO"))
        assertThat(decoded.getDecodedData(), `is`("No PDOL included"))
        assertThat(decoded.isComposite(), `is`(false))
    }
}
