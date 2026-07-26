package com.example.acousticsense.ui.duplex
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.example.acousticsense.duplex.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun DuplexScreen(state:DuplexUiState,onBack:()->Unit,onStart:()->Unit,onStop:()->Unit,onPulse:()->Unit,onRefresh:()->Unit,onBeginGuided:()->Unit,onNext:()->Unit,onComplete:(TestStatus)->Unit,onCancel:()->Unit,onExport:()->Unit){
 Scaffold(topBar={TopAppBar(title={Text("Laboratorio full-duplex")},navigationIcon={TextButton(onClick=onBack){Text("Volver")}})}){p->LazyColumn(Modifier.fillMaxSize().padding(p),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 item{Text("Experimental: no es una ayuda de movilidad",style=MaterialTheme.typography.titleLarge,modifier=Modifier.semantics{heading()});Text("Usar con visión disponible, teléfono apoyado y sin caminar. El nivel digital no equivale a SPL seguro.")}
 item{Card(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Text("Antes de comenzar",style=MaterialTheme.typography.titleMedium);Text("• Teléfono apoyado\n• Bluetooth y auriculares desconectados\n• Volumen bajo\n• Entorno relativamente silencioso\n• No confiar en la prueba para movilidad")}}}
 item{Text("Permiso: ${if(state.permissionGranted)"concedido" else "no concedido"}");Text("Estado: ${state.state}",modifier=Modifier.semantics{liveRegion=LiveRegionMode.Polite});Text("Solicitado: LowLatency, Exclusive → Shared, mono Float, frecuencia natural, Unprocessed → VoiceRecognition")}
 item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick=onStart,enabled=state.permissionGranted&&state.state!=DuplexState.STARTED,modifier=Modifier.weight(1f).heightIn(min=56.dp)){Text("Iniciar")};Button(onClick=onStop,enabled=state.state in setOf(DuplexState.STARTED,DuplexState.STARTING,DuplexState.ERROR),modifier=Modifier.weight(1f).heightIn(min=56.dp)){Text("Detener")}}}
 item{Button(onClick=onPulse,enabled=state.canPulse,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp).semantics{contentDescription="Emitir un único pulso audible de prueba de hasta cien milisegundos"}){Text("Emitir pulso audible de prueba")}}
 item{OutlinedButton(onClick=onRefresh,enabled=state.state==DuplexState.STARTED,modifier=Modifier.fillMaxWidth()){Text("Actualizar métricas")};Text("Configuración real y métricas nativas (sin PCM):");Text(state.nativeSnapshot,style=MaterialTheme.typography.bodySmall);state.lastError?.let{Text("Error: $it")}}
 item{HorizontalDivider();Text("Modo manual o batería guiada",style=MaterialTheme.typography.titleMedium);if(state.session==null)Button(onClick=onBeginGuided,modifier=Modifier.fillMaxWidth()){Text("Crear batería guiada")}}
 state.session?.let{s->item{Text("Sesión: ${s.sessionId}");s.results.forEach{Text("${it.definition.id}: ${it.status.name.lowercase()}")};Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick=onNext){Text("Siguiente")};OutlinedButton(onClick={onComplete(TestStatus.PASSED)}){Text("Aprobar")};OutlinedButton(onClick={onComplete(TestStatus.INCONCLUSIVE)}){Text("Inconclusa")}};TextButton(onClick=onCancel){Text("Cancelar batería")}}}
 item{Button(onClick=onExport,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)){Text("Exportar informe JSON")};Text("El selector del sistema se abre solo por esta acción. No se guarda audio ni se usa Internet.",style=MaterialTheme.typography.bodySmall)}
 }}
}
