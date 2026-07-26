package com.example.acousticsense.duplex

import org.junit.Assert.*
import org.junit.Test

class SessionJsonSerializerTest {
    @Test fun `schema 1_1 exports retained configuration and metrics`() { val snap=EngineSnapshot(metrics=EngineMetrics(4,5,6,inputXruns=null),actualConfiguration=ActualAudioConfiguration("{\"sampleRate\":48000,\"xrunCount\":-1}","{}")); val json=SessionJsonSerializer.serialize(LaboratorySession("id",1,actual=snap.actualConfiguration,finalEngineSnapshot=snap),"1","abc","{}"); assertTrue(json.contains("\"schemaVersion\":\"1.1\"")); assertTrue(json.contains("48000")); assertTrue(json.contains("\"framesRead\":4")); assertFalse(json.contains("\"xrunCount\":-1")); assertTrue(json.contains("\"xrunCount\":null")) }
    @Test fun `report contains no raw audio or sample arrays`() { val json=SessionJsonSerializer.serialize(LaboratorySession("id",1),"1","b","{}"); assertFalse(json.contains("pcm",true)); assertFalse(json.contains("samples",true)); assertFalse(json.contains("wav",true)) }
    @Test fun `escaping produces valid bounded strings`() { val json=SessionJsonSerializer.serialize(LaboratorySession("a\"b",1),"1","b","{}"); assertTrue(json.contains("a\\\"b")) }
}
