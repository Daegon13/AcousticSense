package com.example.acousticsense.duplex
import org.junit.Assert.*
import org.junit.Test
class SessionJsonSerializerTest{
 @Test fun `schema and required sections are serialized without raw audio`(){val json=SessionJsonSerializer.serialize(LaboratorySession("abc",1),"1.0","debug","{}","{\"metrics\":{}}") ;assertTrue(json.contains("\"schemaVersion\":\"${SessionJsonSerializer.SCHEMA_VERSION}\""));assertTrue(json.contains("\"results\""));assertTrue(json.contains("\"events\""));assertFalse(json.contains("pcm",ignoreCase=true));assertFalse(json.contains("samples",ignoreCase=true))}
 @Test fun `escaping keeps JSON strings bounded`(){val json=SessionJsonSerializer.serialize(LaboratorySession("a\"b",1),"1","b","{}","{}");assertTrue(json.contains("a\\\"b"))}
}
