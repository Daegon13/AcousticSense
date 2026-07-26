package com.example.acousticsense.signal

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

class SignalPhaseTest {
 @Test fun `chirp has exact frames hann endpoints finite and bounded`() { val c=ChirpConfiguration();val x=ChirpGenerator.generate(c);assertEquals(480,x.size);assertEquals(0f,x.first(),0f);assertEquals(0f,x.last(),1e-6f);assertTrue(x.all{it.isFinite()&&kotlin.math.abs(it)<=.05f}) }
 @Test fun `configuration rejects nyquist amplitude duration and frequency order`() { listOf(ChirpConfiguration(endFrequencyHz=24_000.0),ChirpConfiguration(amplitude=.051),ChirpConfiguration(durationMs=0.0),ChirpConfiguration(startFrequencyHz=12000.0,endFrequencyHz=4000.0)).forEach { assertThrows(IllegalArgumentException::class.java){it.validate()} } }
 @Test fun `pcm conversion handles endpoints and wav header`() { assertEquals(Short.MIN_VALUE,Pcm16Wav.sample(-1f));assertEquals(0,Pcm16Wav.sample(0f).toInt());assertEquals(Short.MAX_VALUE,Pcm16Wav.sample(1f));val w=Pcm16Wav.encode(floatArrayOf(-1f,0f,1f),48000,1);assertEquals("RIFF",String(w,0,4));assertEquals("WAVE",String(w,8,4));assertEquals(50,w.size);assertEquals(42,w[4].toInt() and 255);assertEquals(0x80,w[24].toInt() and 255);assertEquals(0xbb,w[25].toInt() and 255) }
 @Test fun `series is bounded delayed and cancellable`() { var t=0L;val s=ChirpSchedule{t};assertThrows(IllegalArgumentException::class.java){s.start(SeriesConfiguration(6,1000))};assertThrows(IllegalArgumentException::class.java){s.start(SeriesConfiguration(2,999))};s.start(SeriesConfiguration(2,1000));assertTrue(s.consume());assertFalse(s.consume());t=1000;assertTrue(s.consume());assertFalse(s.active);s.start(SeriesConfiguration(2,1000));s.onBackground();assertEquals(0,s.remaining) }
 @Test fun `csv escapes and contains one row per emission`() { val e=emission("a,\"b");val csv=SignalSessionExport.csv(session(listOf(e,e.copy(chirpId="2"))));assertEquals(3,csv.lines().filter{it.isNotEmpty()}.size);assertTrue(csv.contains("\"a,\"\"b\"")) }
 @Test fun `zip has expected files checksums and json has no pcm arrays`() { val out=ByteArrayOutputStream();SignalSessionExport.writeZip(session(listOf(emission("1"))),"1","abc",JSONObject(),out);val names=mutableSetOf<String>();ZipInputStream(out.toByteArray().inputStream()).use{z->while(true){val e=z.nextEntry?:break;names+=e.name}};assertEquals(setOf("session.json","emissions.csv","input-capture.wav","transmitted-reference.wav"),names);val json=SignalSessionExport.json(session(emptyList()),"1","b",JSONObject());assertFalse(json.contains("pcm",true));assertFalse(json.contains("samples",true));assertTrue(json.contains("2.0")) }
 private fun emission(id:String)=ChirpEmission(id,0,1,ChirpConfiguration(),inputSampleRateHz=48000,outputSampleRateHz=48000,status=EmissionStatus.COMPLETED)
 private fun session(e:List<ChirpEmission>)=SignalSessionSnapshot("s",1,2,chirpConfiguration=ChirpConfiguration(),captureConfiguration=CaptureConfiguration(),inputSampleRateHz=48000,outputSampleRateHz=48000,emissions=e,capture=floatArrayOf(0f),transmittedReference=floatArrayOf(0f))
}
