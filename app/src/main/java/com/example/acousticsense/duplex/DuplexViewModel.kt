package com.example.acousticsense.duplex
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID
class DuplexViewModel(private val engine:DuplexEngine,private val post:((()->Unit)->Unit)={Handler(Looper.getMainLooper())::post},private val runner:LaboratorySessionRunner=LaboratorySessionRunner()):ViewModel(){
 var state by mutableStateOf(DuplexUiState());private set
 fun updatePermission(granted:Boolean){if(!granted)stop();state=state.copy(permissionGranted=granted,lastError=if(granted)null else "Se requiere permiso de micrófono")}
 fun start(){if(!state.permissionGranted){state=state.copy(state=DuplexState.ERROR,lastError="Permiso de micrófono ausente");return};if(state.state==DuplexState.STARTED||state.state==DuplexState.STARTING)return;state=state.copy(state=DuplexState.STARTING,lastError=null);post{if(engine.start()){state=state.copy(state=DuplexState.STARTED,nativeSnapshot=engine.snapshotJson())}else state=state.copy(state=DuplexState.ERROR,lastError="No se pudieron abrir e iniciar ambos streams",nativeSnapshot=engine.snapshotJson())}}
 fun stop(){if(state.state in setOf(DuplexState.IDLE,DuplexState.STOPPED,DuplexState.STOPPING))return;state=state.copy(state=DuplexState.STOPPING);engine.stop();state=state.copy(state=DuplexState.STOPPED,nativeSnapshot=engine.snapshotJson())}
 fun pulse(){if(state.canPulse&&!engine.emitPulse())state=state.copy(lastError="El pulso fue rechazado porque ambos streams no están iniciados") else refresh()}
 fun refresh(){if(state.state==DuplexState.STARTED)state=state.copy(nativeSnapshot=engine.snapshotJson())}
 fun beginGuided(){state=state.copy(guidedMode=true,session=runner.create(UUID.randomUUID().toString()))}
 fun nextTest(){state.session?.let{state=state.copy(session=runner.startNext(it))}}
 fun completeTest(status:TestStatus,message:String?=null){state.session?.let{state=state.copy(session=runner.complete(it,status,message))}}
 fun cancelGuided(){state.session?.let{state=state.copy(session=runner.cancel(it))};stop()}
 fun exportJson(appVersion:String,build:String,deviceJson:String)=SessionJsonSerializer.serialize(state.session?:runner.create(UUID.randomUUID().toString()),appVersion,build,deviceJson,engine.snapshotJson())
 override fun onCleared(){engine.close()}
}
