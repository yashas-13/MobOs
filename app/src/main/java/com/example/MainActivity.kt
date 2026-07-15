package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppDatabase
import com.example.data.WorkflowEntity
import com.example.data.WorkflowRepository
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkPanel
import com.example.ui.theme.CyberGrayText
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberOrange
import com.example.ui.theme.CyberPanelBorder
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberWhite
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val viewModel: AgentViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = WorkflowRepository(db.workflowDao())
        AgentViewModelFactory(application, repository)
    }

    override fun dispatchKeyEvent(event: android.view.KeyEvent): Boolean {
        if (event.action == android.view.KeyEvent.ACTION_DOWN) {
            val isCtrl = event.isCtrlPressed
            val isShift = event.isShiftPressed
            val isAlt = event.isAltPressed
            
            if (isCtrl || isAlt) {
                when (event.keyCode) {
                    android.view.KeyEvent.KEYCODE_T -> {
                        viewModel.triggerToggleTerminal()
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_I -> {
                        viewModel.triggerFocusChatInput()
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_P -> {
                        viewModel.toggleProgressTracker()
                        return true
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                // Observe side-effect events (LaunchIntents, Toast alerts)
                LaunchedEffect(Unit) {
                    viewModel.events.collectLatest { event ->
                        when (event) {
                            is AgentEvent.LaunchIntent -> {
                                try {
                                    context.startActivity(event.intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No app found to handle this workflow action.", Toast.LENGTH_SHORT).show()
                                    viewModel.addLog("⚠️ Failed to launch system app client: ${e.message}")
                                }
                            }
                            is AgentEvent.ShowToast -> {
                                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    MainConsoleScreen(viewModel)
                    SecurityApprovalModal(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainConsoleScreen(viewModel: AgentViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val tabTitles = listOf("Console", "Super Agent", "Repository", "Agents & Server", "Workflows", "Integrations")

    // Retrieve system bars insets for edge-to-edge support
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBlack)
                .padding(top = statusBarPadding.calculateTopPadding())
        ) {
            // Holographic System Header Banner
            SystemHologramHeader(viewModel)

            // Custom futuristic M3 TabRow
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CyberDarkPanel,
                contentColor = CyberCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberCyan,
                        height = 2.dp
                    )
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CyberPanelBorder)
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title.uppercase(),
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            )
                        },
                        selectedContentColor = CyberCyan,
                        unselectedContentColor = CyberGrayText
                    )
                }
            }

            // Tab Content Pane
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = navBarPadding.calculateBottomPadding())
            ) {
                when (selectedTab) {
                    0 -> ConsoleTabContent(viewModel)
                    1 -> SuperAgentTabContent(viewModel)
                    2 -> RepositoryTabContent(viewModel)
                    3 -> AgentsServerTabContent(viewModel)
                    4 -> WorkflowsTabContent(viewModel)
                    5 -> IntegrationsTabContent(viewModel)
                }

                // Floating system-level notification stack
                SystemNotificationOverlay(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SystemHologramHeader(viewModel: AgentViewModel) {
    val agentStatus by viewModel.agentStatus.collectAsState()

    // Blinking pulsing alpha animation for high-tech telemetry banner
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val statusColor = when (agentStatus) {
        "EXECUTING" -> CyberOrange
        "ONLINE" -> CyberGreen
        else -> CyberCyan
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberDarkPanel)
            .border(BorderStroke(1.dp, CyberPanelBorder))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "🤖 AGENTIC OS CONTROL CENTER",
                color = CyberCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "SYSTEM ENGINE: GEMINI-3.5-FLASH-CORE",
                color = CyberGrayText,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }

        // Live animated Status Orb
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                .background(CyberBlack)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(if (agentStatus == "EXECUTING") pulseAlpha else 1.0f)
                    .background(statusColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = agentStatus,
                color = statusColor,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun NluAnalysisPanel(
    nlu: NluAnalysisState,
    onResolve: (String) -> Unit
) {
    var clarificationInput by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
        border = BorderStroke(1.dp, if (nlu.ambiguityScore > 0.6f) CyberRed else CyberCyan),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔬 ACTIVE NLU ENGINE PARSE ANALYSIS",
                    color = if (nlu.ambiguityScore > 0.6f) CyberRed else CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                // Target platform badge
                Box(
                    modifier = Modifier
                        .background(CyberBlack, RoundedCornerShape(4.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = nlu.targetPlatform.uppercase(),
                        color = CyberGreen,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Ambiguity Heatmap
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AMBIGUITY LEVEL: ",
                    color = CyberGrayText,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
                
                val levelColor = when {
                    nlu.ambiguityScore > 0.6f -> CyberRed
                    nlu.ambiguityScore > 0.3f -> CyberOrange
                    else -> CyberGreen
                }
                
                Text(
                    text = "${(nlu.ambiguityScore * 100).toInt()}%",
                    color = levelColor,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Mini bar graph indicator
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .weight(1f)
                        .background(CyberBlack, RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(nlu.ambiguityScore.coerceIn(0.01f, 1.0f))
                            .background(levelColor, RoundedCornerShape(3.dp))
                    )
                }
            }
            
            // Clarification Warning Sheet
            if (nlu.ambiguityScore > 0.6f && nlu.clarificationPrompt.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberRed.copy(alpha = 0.08f))
                        .border(BorderStroke(1.dp, CyberRed.copy(alpha = 0.4f)), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "🎙️ CLARIFICATION INSTRUCTION REQUIRED:",
                            color = CyberRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nlu.clarificationPrompt,
                            color = CyberWhite,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = clarificationInput,
                                onValueChange = { clarificationInput = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Provide details (recipient, topic...)", fontSize = 11.sp, color = CyberGrayText) },
                                singleLine = true,
                                textStyle = TextStyle(color = CyberWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberRed,
                                    unfocusedBorderColor = CyberPanelBorder,
                                    cursorColor = CyberRed,
                                    focusedContainerColor = CyberBlack,
                                    unfocusedContainerColor = CyberBlack
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (clarificationInput.isNotBlank()) {
                                        onResolve(clarificationInput)
                                        clarificationInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberRed, contentColor = CyberWhite),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("RESOLVE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            
            // Extracted Entities Grid
            if (nlu.extractedEntities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🎯 EXTRACTED CONTEXTUAL ENTITIES:",
                    color = CyberGrayText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    nlu.extractedEntities.forEach { (key, valStr) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberBlack.copy(alpha = 0.5f))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = key,
                                color = CyberCyan,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = valStr,
                                color = CyberWhite,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            
            // Generated Shell Script
            if (nlu.generatedShellScript.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "💻 GENERATED TERMINAL SHELL AUTOMATION:",
                    color = CyberGrayText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberBlack)
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = nlu.generatedShellScript,
                        color = CyberGreen,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConsoleTabContent(viewModel: AgentViewModel) {
    val logs by viewModel.terminalLogs.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val nlu by viewModel.nluAnalysis.collectAsState()
    var commandText by remember { mutableStateOf("") }
    
    val histories by viewModel.allHistories.collectAsState(initial = emptyList())
    var showHistoryOverlay by remember { mutableStateOf(false) }
    var historySearchText by remember { mutableStateOf("") }
    
    val terminalListState = rememberLazyListState()
    val chatListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.focusChatInputEvent.collect {
            focusRequester.requestFocus()
        }
    }

    val activeMode by viewModel.activeMode.collectAsState()

    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val voiceVolumeLevels by viewModel.voiceVolumeLevels.collectAsState()
    val speechTranscript by viewModel.speechTranscript.collectAsState()

    val suggestions = listOf(
        "deploy python backend microservice",
        "run container diagnostics",
        "deploy success",
        "issue report sync",
        "plan trip to london",
        "timer 10s"
    )

    // Auto-scroll on updates
    LaunchedEffect(logs.size, activeMode) {
        if (activeMode == "TERMINAL" && logs.isNotEmpty()) {
            scope.launch {
                terminalListState.animateScrollToItem(logs.size - 1)
            }
        }
    }
    LaunchedEffect(chatMessages.size, activeMode) {
        if (activeMode == "CHAT" && chatMessages.isNotEmpty()) {
            scope.launch {
                chatListState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Voice active audio interpreter visualizer
        if (isRecordingVoice) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberRed.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, CyberRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(CyberRed, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LIVE AUDIO INTERPRETER ACTIVE",
                            color = CyberRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Wave visualizer based on live voice levels
                    Row(
                        modifier = Modifier
                            .height(36.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (voiceVolumeLevels.isNotEmpty()) {
                            voiceVolumeLevels.forEach { level ->
                                val barHeight = (32 * level).dp.coerceAtLeast(4.dp)
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .width(5.dp)
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(2.5.dp))
                                        .background(CyberRed)
                                )
                            }
                        } else {
                            repeat(8) {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .width(5.dp)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(2.5.dp))
                                        .background(CyberRed.copy(alpha = 0.3f))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (speechTranscript.isBlank()) "Listening..." else "\"$speechTranscript\"",
                        color = CyberWhite,
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Dual-Mode Selection Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(CyberBlack, RoundedCornerShape(8.dp))
                .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (activeMode == "CHAT") CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                    .border(BorderStroke(1.dp, if (activeMode == "CHAT") CyberCyan else Color.Transparent), RoundedCornerShape(6.dp))
                    .clickable { viewModel.setActiveMode("CHAT") }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💬", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SMART CHAT ASSISTANT",
                        color = if (activeMode == "CHAT") CyberWhite else CyberGrayText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (activeMode == "TERMINAL") CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                    .border(BorderStroke(1.dp, if (activeMode == "TERMINAL") CyberCyan else Color.Transparent), RoundedCornerShape(6.dp))
                    .clickable { viewModel.setActiveMode("TERMINAL") }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💻", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TERMINAL COMPILER STACKS",
                        color = if (activeMode == "TERMINAL") CyberWhite else CyberGrayText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Global Keyboard Shortcut HUD / Controller Panel
        var showShortcutHUD by remember { mutableStateOf(true) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
            border = BorderStroke(1.dp, if (showShortcutHUD) CyberCyan else CyberPanelBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showShortcutHUD = !showShortcutHUD },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⌨️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GLOBAL KEYBOARD SHORTCUTS MANAGER",
                            color = if (showShortcutHUD) CyberCyan else CyberWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = if (showShortcutHUD) "COLLAPSE ▲" else "EXPAND HUD ▼",
                        color = CyberGrayText,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (showShortcutHUD) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Trigger actions below instantly via hardware keys or tap the live controller switches:",
                        color = CyberGrayText,
                        fontSize = 9.sp,
                        lineHeight = 12.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Shortcut 1: Trigger Terminal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberBlack, RoundedCornerShape(4.dp))
                                .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                                .clickable { viewModel.triggerToggleTerminal() }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Toggle Terminal Mode",
                                    color = CyberWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Switches mode between Chat and Compiler terminal stacks.",
                                    color = CyberGrayText,
                                    fontSize = 8.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .border(BorderStroke(1.dp, CyberCyan), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ALT + T",
                                    color = CyberCyan,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Shortcut 2: Focus Chat Console
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberBlack, RoundedCornerShape(4.dp))
                                .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                                .clickable { viewModel.triggerFocusChatInput() }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Focus Chat Console",
                                    color = CyberWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Switches view to Console tab, CHAT mode, and requests input focus.",
                                    color = CyberGrayText,
                                    fontSize = 8.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .border(BorderStroke(1.dp, CyberCyan), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ALT + I",
                                    color = CyberCyan,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Shortcut 3: Toggle Progress Tracker
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberBlack, RoundedCornerShape(4.dp))
                                .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                                .clickable { viewModel.toggleProgressTracker() }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Toggle Cognitive Progress Tracker",
                                    color = CyberWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Toggles visibility of the Step-by-Step Orchestration progress panel.",
                                    color = CyberGrayText,
                                    fontSize = 8.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val isTrackerVisible by viewModel.isProgressTrackerVisible.collectAsState()
                                Text(
                                    text = if (isTrackerVisible) "ACTIVE" else "HIDDEN",
                                    color = if (isTrackerVisible) CyberGreen else CyberOrange,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .border(BorderStroke(1.dp, CyberCyan), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "ALT + P",
                                        color = CyberCyan,
                                        fontSize = 8.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Main Display Section
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (activeMode == "CHAT") {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SECURE MULTI-AGENT CHAT",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // History Toggle Badge Button
                            Box(
                                modifier = Modifier
                                    .background(if (showHistoryOverlay) CyberCyan.copy(alpha = 0.15f) else CyberDarkPanel, RoundedCornerShape(4.dp))
                                    .border(BorderStroke(1.dp, if (showHistoryOverlay) CyberCyan else CyberPanelBorder), RoundedCornerShape(4.dp))
                                    .clickable { showHistoryOverlay = !showHistoryOverlay }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "📜 HISTORY (${histories.size})",
                                    color = if (showHistoryOverlay) CyberCyan else CyberGrayText,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "CLEAR SESSION",
                                color = CyberRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .clickable { viewModel.clearChat() }
                                    .padding(4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    if (showHistoryOverlay) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberBlack),
                            border = BorderStroke(1.dp, CyberCyan),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚡ PERSISTED COMMAND WORKFLOW HISTORY",
                                        color = CyberCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    Text(
                                        text = "✕ CLOSE",
                                        color = CyberRed,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.clickable { showHistoryOverlay = false }
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // History Search Field
                                OutlinedTextField(
                                    value = historySearchText,
                                    onValueChange = { historySearchText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    placeholder = {
                                        Text(
                                            text = "Search historical commands...",
                                            color = CyberGrayText,
                                            fontSize = 11.sp
                                        )
                                    },
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        color = CyberWhite,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    ),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyberCyan,
                                        unfocusedBorderColor = CyberPanelBorder,
                                        focusedContainerColor = CyberDarkPanel,
                                        unfocusedContainerColor = CyberDarkPanel
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                val filteredHistories = if (historySearchText.isBlank()) {
                                    histories
                                } else {
                                    histories.filter {
                                        it.commandText.contains(historySearchText, ignoreCase = true) ||
                                                it.workflowName.contains(historySearchText, ignoreCase = true)
                                    }
                                }

                                if (filteredHistories.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "NO COMPATIBLE HISTORICAL WORKFLOWS FOUND",
                                            color = CyberGrayText,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        items(filteredHistories) { item ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(CyberDarkPanel, RoundedCornerShape(4.dp))
                                                    .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = item.commandText,
                                                        color = CyberWhite,
                                                        fontSize = 10.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "${item.workflowName.uppercase()} • ${if (item.status == "SUCCESS") "✅" else "❌"} ${item.status}",
                                                        color = if (item.status == "SUCCESS") CyberGreen else CyberRed,
                                                        fontSize = 8.sp,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }

                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    // Auto-fill button
                                                    Box(
                                                        modifier = Modifier
                                                            .background(CyberBlack, RoundedCornerShape(3.dp))
                                                            .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(3.dp))
                                                            .clickable {
                                                                commandText = item.commandText
                                                                showHistoryOverlay = false
                                                            }
                                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                                    ) {
                                                        Text("LOAD", color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                                    }

                                                    // Re-execute button
                                                    Box(
                                                        modifier = Modifier
                                                            .background(CyberCyan, RoundedCornerShape(3.dp))
                                                            .clickable {
                                                                commandText = ""
                                                                viewModel.processCommand(item.commandText)
                                                                showHistoryOverlay = false
                                                            }
                                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                                    ) {
                                                        Text("EXECUTE", color = CyberBlack, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    LazyColumn(
                        state = chatListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(8.dp))
                            .background(CyberDarkPanel)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(chatMessages) { message ->
                            ChatMessageRow(message = message)
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "TELEMETRY STACKS & COMPILER OUTPUTS",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(8.dp))
                            .background(CyberDarkPanel)
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            state = terminalListState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(logs) { log ->
                                val color = when {
                                    log.contains("❌") -> CyberRed
                                    log.contains("⚠️") -> CyberOrange
                                    log.contains("✅") -> CyberGreen
                                    log.contains("🤖") || log.contains("🧩") -> CyberCyan
                                    log.contains("⚡") -> CyberWhite
                                    else -> CyberGrayText
                                }

                                Text(
                                    text = log,
                                    color = color,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.clearLogs() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(28.dp)
                                .background(CyberBlack.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Clear logs",
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Advanced NLU parsing panel
        if (nlu.parsedSuccessfully) {
            Spacer(modifier = Modifier.height(8.dp))
            NluAnalysisPanel(nlu = nlu, onResolve = { detail ->
                viewModel.resolveAmbiguity(detail)
            })
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Global Keyboard Shortcut Dashboard Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
            border = BorderStroke(1.dp, CyberPanelBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⌨️ GLOBAL HOTKEY MANAGER",
                    color = CyberCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShortcutBadge(key = "Ctrl+T", desc = "Terminal") { viewModel.triggerToggleTerminal() }
                    ShortcutBadge(key = "Ctrl+I", desc = "Focus") { viewModel.triggerFocusChatInput() }
                    ShortcutBadge(key = "Ctrl+P", desc = "Tracker") { viewModel.toggleProgressTracker() }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Suggestion Assist Chips
        Text(
            text = "QUICK TRIGGER COMMAND SHELLS",
            color = CyberGrayText,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            maxItemsInEachRow = 3
        ) {
            suggestions.forEach { label ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                        .background(CyberDarkPanel)
                        .clickable {
                            commandText = label
                            viewModel.processCommand(label)
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input Console Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice Microphone Toggle Button
            IconButton(
                onClick = { viewModel.toggleVoiceRecording() },
                modifier = Modifier
                    .size(52.dp)
                    .background(if (isRecordingVoice) CyberRed.copy(alpha = 0.15f) else CyberDarkPanel, RoundedCornerShape(8.dp))
                    .border(BorderStroke(1.dp, if (isRecordingVoice) CyberRed else CyberPanelBorder), RoundedCornerShape(8.dp))
                    .testTag("voice_mic_button")
            ) {
                Text(
                    text = if (isRecordingVoice) "🛑" else "🎙️",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = commandText,
                onValueChange = { commandText = it },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .testTag("command_input"),
                placeholder = {
                    Text(
                        text = "Initialize natural language workflow...",
                        color = CyberGrayText,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = CyberCyan
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = CyberWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberPanelBorder,
                    cursorColor = CyberCyan,
                    focusedContainerColor = CyberDarkPanel,
                    unfocusedContainerColor = CyberDarkPanel
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (commandText.isNotBlank()) {
                        viewModel.processCommand(commandText)
                        commandText = ""
                    }
                },
                modifier = Modifier
                    .height(52.dp)
                    .testTag("submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = CyberBlack
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Execute command",
                    tint = CyberBlack
                )
            }
        }
    }
}

@Composable
fun ChatMessageRow(message: ChatMessage) {
    val isUser = message.sender == "USER"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) CyberBlack else CyberBlack
    val borderColor = if (isUser) CyberCyan.copy(alpha = 0.5f) else CyberPanelBorder
    val textColor = if (isUser) CyberWhite else CyberWhite
    
    val timeFormat = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
    val timeString = timeFormat.format(java.util.Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            if (!isUser) {
                // Agent head indicator
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(CyberCyan.copy(alpha = 0.15f), CircleShape)
                        .border(BorderStroke(1.dp, CyberCyan), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤖", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SUPER AGENT",
                    color = CyberCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = timeString,
                    color = CyberGrayText,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                Text(
                    text = timeString,
                    color = CyberGrayText,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "USER",
                    color = CyberWhite,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(CyberWhite.copy(alpha = 0.15f), CircleShape)
                        .border(BorderStroke(1.dp, CyberWhite.copy(alpha = 0.5f)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(bubbleColor, RoundedCornerShape(8.dp))
                .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            if (isUser) {
                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 15.sp
                )
            } else {
                ChatTextFormatter(text = message.text)
            }
        }
    }
}

@Composable
fun ChatTextFormatter(text: String) {
    val lines = text.split("\n")
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        var inCodeBlock = false
        val codeLines = mutableListOf<String>()

        lines.forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.startsWith("```")) {
                if (inCodeBlock) {
                    // Render code block
                    CodeBlock(code = codeLines.joinToString("\n"))
                    codeLines.clear()
                    inCodeBlock = false
                } else {
                    inCodeBlock = true
                }
            } else if (inCodeBlock) {
                codeLines.add(line)
            } else if (trimmedLine.startsWith("- ")) {
                Row(verticalAlignment = Alignment.Top) {
                    Text("•", color = CyberCyan, fontSize = 11.sp, modifier = Modifier.padding(end = 6.dp, top = 2.dp))
                    Text(
                        text = line.substring(2).replace("**", "").replace("*", ""),
                        color = CyberWhite,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            } else if (trimmedLine.isNotBlank()) {
                // Highlight links and headers or bold markers
                Text(
                    text = line.replace("**", "").replace("*", ""),
                    color = if (trimmedLine.startsWith("###") || trimmedLine.startsWith("##")) CyberCyan else CyberWhite,
                    fontSize = if (trimmedLine.startsWith("###") || trimmedLine.startsWith("##")) 12.sp else 11.sp,
                    fontWeight = if (trimmedLine.startsWith("###") || trimmedLine.startsWith("##")) FontWeight.Bold else FontWeight.Normal,
                    lineHeight = 15.sp,
                    fontFamily = FontFamily.SansSerif
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        if (inCodeBlock && codeLines.isNotEmpty()) {
            CodeBlock(code = codeLines.joinToString("\n"))
        }
    }
}

@Composable
fun CodeBlock(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberBlack, RoundedCornerShape(4.dp))
            .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
            .padding(6.dp)
    ) {
        Text(
            text = code,
            color = CyberGreen,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 13.sp
        )
    }
}

data class PredefinedTemplate(
    val name: String,
    val icon: String,
    val trigger: String,
    val description: String,
    val steps: List<String>,
    val stepsJson: String
)

@Composable
fun WorkflowsTabContent(viewModel: AgentViewModel) {
    val workflows by viewModel.allWorkflows.collectAsState(initial = emptyList())
    val histories by viewModel.allHistories.collectAsState(initial = emptyList())

    var showCreator by remember { mutableStateOf(false) }

    // State for creating a custom workflow
    var newWorkflowName by remember { mutableStateOf("") }
    var newWorkflowTrigger by remember { mutableStateOf("") }
    var newWorkflowStepsJson by remember { mutableStateOf("") }

    var selectedHistoryLogText by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Custom Workflow Creator Banner ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(1.dp, CyberPanelBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCreator = !showCreator },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛠️ CREATE CUSTOM AUTOMATION WORKFLOW",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = if (showCreator) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle creator",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    AnimatedVisibility(visible = showCreator) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            OutlinedTextField(
                                value = newWorkflowName,
                                onValueChange = { newWorkflowName = it },
                                label = { Text("Workflow Name", fontSize = 11.sp, color = CyberGrayText) },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 12.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberCyan,
                                    unfocusedBorderColor = CyberPanelBorder
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newWorkflowTrigger,
                                onValueChange = { newWorkflowTrigger = it },
                                label = { Text("Command Trigger Keyword", fontSize = 11.sp, color = CyberGrayText) },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberCyan,
                                    unfocusedBorderColor = CyberPanelBorder
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newWorkflowStepsJson,
                                onValueChange = { newWorkflowStepsJson = it },
                                label = { Text("Steps (JSON Array)", fontSize = 11.sp, color = CyberGrayText) },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                placeholder = {
                                    Text(
                                        text = "[{\"actionType\":\"NOTIFY\",\"parameters\":{\"title\":\"Alert\",\"message\":\"Success\"}}]",
                                        fontSize = 10.sp,
                                        color = CyberGrayText,
                                        fontFamily = FontFamily.Monospace
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberCyan,
                                    unfocusedBorderColor = CyberPanelBorder
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (newWorkflowName.isNotBlank() && newWorkflowTrigger.isNotBlank() && newWorkflowStepsJson.isNotBlank()) {
                                        viewModel.addCustomWorkflow(newWorkflowName, newWorkflowTrigger, newWorkflowStepsJson)
                                        newWorkflowName = ""
                                        newWorkflowTrigger = ""
                                        newWorkflowStepsJson = ""
                                        showCreator = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("REGISTER WORKFLOW", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Predefined Automation Template Library ---
        item {
            Text(
                text = "📚 PREDEFINED AUTOMATION TEMPLATE LIBRARY",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        item {
            val templates = listOf(
                PredefinedTemplate(
                    name = "Weekly Dependency Updater",
                    icon = "🔄",
                    trigger = "update dependencies weekend",
                    description = "Locks & updates project poetry / npm dependencies safe-upgrades.",
                    steps = listOf("OS_COMMAND", "NOTIFY"),
                    stepsJson = """[{"actionType":"OS_COMMAND","parameters":{"platform":"LINUX","command":"poetry update && npm audit fix","script_body":"poetry update && npm audit fix"}},{"actionType":"NOTIFY","parameters":{"title":"Dependency Monitor","message":"Upgrade run complete."}}]"""
                ),
                PredefinedTemplate(
                    name = "Commit Security Scanner",
                    icon = "🛡️",
                    trigger = "run security scanner",
                    description = "Scans codebase topology for exposed keys or weak port assignments.",
                    steps = listOf("OS_COMMAND", "NOTIFY"),
                    stepsJson = """[{"actionType":"OS_COMMAND","parameters":{"platform":"LINUX","command":"gitleaks detect --source=. --verbose","script_body":"gitleaks detect"}},{"actionType":"NOTIFY","parameters":{"title":"Security Gate","message":"0 secrets exposed."}}]"""
                ),
                PredefinedTemplate(
                    name = "Release Notes Generator",
                    icon = "📝",
                    trigger = "generate release notes",
                    description = "Aggregates Git logs & constructs an automated release report summary.",
                    steps = listOf("SEARCH", "EMAIL", "NOTIFY"),
                    stepsJson = """[{"actionType":"SEARCH","parameters":{"query":"git log summary"}},{"actionType":"EMAIL","parameters":{"recipient":"leads@company.com","subject":"Release Note v2.4.1","body":"Summary report"}},{"actionType":"NOTIFY","parameters":{"title":"Release Agent","message":"Release note sent."}}]"""
                ),
                PredefinedTemplate(
                    name = "Staging Deploy Pipeline",
                    icon = "🚀",
                    trigger = "deploy staging tests",
                    description = "Runs integration verification and deploys Docker stack to port 8082.",
                    steps = listOf("OS_COMMAND", "NOTIFY"),
                    stepsJson = """[{"actionType":"OS_COMMAND","parameters":{"platform":"LINUX","command":"pytest && docker-compose staging up -d","script_body":"pytest"}},{"actionType":"NOTIFY","parameters":{"title":"Deploy Specialist","message":"Staging deployed."}}]"""
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                templates.forEach { template ->
                    Card(
                        modifier = Modifier
                            .width(260.dp)
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                        border = BorderStroke(1.dp, CyberPanelBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${template.icon} ${template.name}",
                                    color = CyberWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = template.description,
                                color = CyberGrayText,
                                fontSize = 9.sp,
                                lineHeight = 12.sp,
                                minLines = 2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Visual Pipeline steps flow indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pipeline: ",
                                    color = CyberGrayText,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                template.steps.forEachIndexed { idx, step ->
                                    if (idx > 0) {
                                        Text(text = " ➔ ", color = CyberGrayText, fontSize = 7.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                                            .border(BorderStroke(0.5.dp, CyberCyan.copy(alpha = 0.5f)), RoundedCornerShape(2.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = step,
                                            color = CyberCyan,
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.addCustomWorkflow(template.name, template.trigger, template.stepsJson)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberCyan),
                                    border = BorderStroke(1.dp, CyberCyan),
                                    contentPadding = PaddingValues(vertical = 4.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("📥 ADD", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }

                                Button(
                                    onClick = {
                                        viewModel.processCommand(template.trigger)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack),
                                    contentPadding = PaddingValues(vertical = 4.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("⚡ EXECUTE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Active Workflows Registry Section ---
        item {
            Text(
                text = "⚡ WORKFLOW AUTOMATION REGISTRY",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        if (workflows.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No workflows registered. Create one above!",
                        color = CyberGrayText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else {
            items(workflows) { workflow ->
                WorkflowItemCard(workflow = workflow, onRun = { viewModel.processCommand(workflow.triggerCommand) }, onDelete = { viewModel.deleteWorkflow(workflow) })
            }
        }

        // --- Historic Execution Logs ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📂 HISTORIC RUN TELEMETRY LOGS",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (histories.isNotEmpty()) {
                    Text(
                        text = "CLEAR HISTORY",
                        color = CyberRed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable { viewModel.clearHistory() }
                            .border(BorderStroke(0.5.dp, CyberRed), RoundedCornerShape(2.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        if (histories.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No historic records. Trigger a workflow to inspect execution history.",
                        color = CyberGrayText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        } else {
            items(histories) { history ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedHistoryLogText = if (selectedHistoryLogText == history.logs) null else history.logs
                        },
                    colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                    border = BorderStroke(1.dp, if (history.status == "SUCCESS") CyberGreen.copy(alpha = 0.3f) else CyberRed.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = history.workflowName.uppercase(),
                                    color = CyberWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Command: \"${history.commandText}\"",
                                    color = CyberGrayText,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Status badge
                            Text(
                                text = history.status,
                                color = if (history.status == "SUCCESS") CyberGreen else CyberRed,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Detailed step-by-step telemetry logger
                        AnimatedVisibility(visible = selectedHistoryLogText == history.logs) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                                    .background(CyberBlack)
                                    .border(BorderStroke(0.5.dp, CyberPanelBorder))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "--- STEP DETAILS LOG ---",
                                    color = CyberCyan,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = history.logs,
                                    color = CyberGrayText,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkflowItemCard(workflow: WorkflowEntity, onRun: () -> Unit, onDelete: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
        border = BorderStroke(1.dp, CyberPanelBorder),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workflow.name,
                        color = CyberCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Trigger keyword: ",
                            color = CyberGrayText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "'${workflow.triggerCommand}'",
                            color = CyberWhite,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRun,
                        modifier = Modifier
                            .size(32.dp)
                            .background(CyberCyan.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Trigger workflow",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .background(CyberRed.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete workflow",
                            tint = CyberRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable checklist representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isExpanded) "Hide logical execution steps" else "Inspect logical execution steps",
                    color = CyberGrayText,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = CyberGrayText,
                    modifier = Modifier.size(12.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                val parsedSteps = remember(workflow.stepsJson) {
                    val list = mutableListOf<Pair<String, String>>()
                    var parseError: String? = null
                    try {
                        val stepsArray = JSONArray(workflow.stepsJson)
                        for (i in 0 until stepsArray.length()) {
                            val step = stepsArray.getJSONObject(i)
                            val type = step.optString("actionType", "UNKNOWN")
                            val params = step.optJSONObject("parameters")?.toString() ?: "{}"
                            list.add(Pair(type, params))
                        }
                    } catch (e: Exception) {
                        parseError = e.message ?: "Unknown JSON Error"
                    }
                    Pair(list, parseError)
                }

                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .background(CyberBlack)
                        .border(BorderStroke(0.5.dp, CyberPanelBorder))
                        .padding(8.dp)
                ) {
                    val error = parsedSteps.second
                    if (error != null) {
                        Text(
                            text = "Error parsing steps: $error",
                            color = CyberRed,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        parsedSteps.first.forEachIndexed { i, step ->
                            val type = step.first
                            val params = step.second

                            val icon = when (type.uppercase()) {
                                "EMAIL" -> Icons.Default.Email
                                "MAP" -> Icons.Default.Map
                                "SLACK" -> Icons.Default.Chat
                                "DISCORD" -> Icons.Default.Forum
                                "NOTION" -> Icons.Default.Description
                                "NOTIFY" -> Icons.Default.Notifications
                                "SEARCH" -> Icons.Default.Search
                                "TIMER" -> Icons.Default.Timer
                                else -> Icons.Default.Code
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Step ${i + 1}: $type",
                                        color = CyberWhite,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Params: $params",
                                        color = CyberGrayText,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IntegrationsTabContent(viewModel: AgentViewModel) {
    val slackUrl by viewModel.slackWebhook.collectAsState()
    val discordUrl by viewModel.discordWebhook.collectAsState()
    val notionDbId by viewModel.notionDatabaseId.collectAsState()
    val githubToken by viewModel.githubToken.collectAsState()
    val twilioSid by viewModel.twilioSid.collectAsState()
    val jiraHost by viewModel.jiraHostUrl.collectAsState()
    val sshHost by viewModel.sshHostUrl.collectAsState()

    var slackInput by remember { mutableStateOf(slackUrl) }
    var discordInput by remember { mutableStateOf(discordUrl) }
    var notionInput by remember { mutableStateOf(notionDbId) }
    var githubInput by remember { mutableStateOf(githubToken) }
    var twilioInput by remember { mutableStateOf(twilioSid) }
    var jiraInput by remember { mutableStateOf(jiraHost) }
    var sshInput by remember { mutableStateOf(sshHost) }

    // Sync input fields with database-backed StateFlows when they load
    LaunchedEffect(slackUrl) { slackInput = slackUrl }
    LaunchedEffect(discordUrl) { discordInput = discordUrl }
    LaunchedEffect(notionDbId) { notionInput = notionDbId }
    LaunchedEffect(githubToken) { githubInput = githubToken }
    LaunchedEffect(twilioSid) { twilioInput = twilioSid }
    LaunchedEffect(jiraHost) { jiraInput = jiraHost }
    LaunchedEffect(sshHost) { sshInput = sshHost }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warning Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(1.dp, CyberRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🛡️ SECURITY & API COMPLIANCE WARNING",
                        color = CyberRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Security Warning: I have included your API keys in the generated APK file for this prototype. Please be aware that Android APKs can be easily decompiled, and these keys can be extracted by anyone who has access to the file. Do not share this APK file publicly or with unauthorized individuals to prevent potential misuse.",
                        color = CyberGrayText,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Configuration Banner
        item {
            Text(
                text = "🔌 MULTI-PLATFORM REST ENDPOINTS",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(1.dp, CyberPanelBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = slackInput,
                        onValueChange = { slackInput = it },
                        label = { Text("Slack Custom Webhook", fontSize = 11.sp, color = CyberCyan) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberPanelBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = discordInput,
                        onValueChange = { discordInput = it },
                        label = { Text("Discord Custom Webhook", fontSize = 11.sp, color = CyberCyan) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberPanelBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notionInput,
                        onValueChange = { notionInput = it },
                        label = { Text("Notion Database ID", fontSize = 11.sp, color = CyberCyan) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberPanelBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = githubInput,
                        onValueChange = { githubInput = it },
                        label = { Text("GitHub Custom OAuth Token", fontSize = 11.sp, color = CyberCyan) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberPanelBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = twilioInput,
                        onValueChange = { twilioInput = it },
                        label = { Text("Twilio Account SID (Cellular Gateway)", fontSize = 11.sp, color = CyberCyan) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberPanelBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = jiraInput,
                        onValueChange = { jiraInput = it },
                        label = { Text("Jira Cloud Server Domain", fontSize = 11.sp, color = CyberCyan) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberPanelBorder
                        ),
                        placeholder = { Text("e.g. company.atlassian.net", fontSize = 11.sp, color = CyberGrayText) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = sshInput,
                        onValueChange = { sshInput = it },
                        label = { Text("SSH Secure Shell Host Endpoint", fontSize = 11.sp, color = CyberCyan) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = CyberWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberPanelBorder
                        ),
                        placeholder = { Text("e.g. user@ssh.agentic-os.net:22", fontSize = 11.sp, color = CyberGrayText) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.updateIntegrationConfig(
                                slackInput,
                                discordInput,
                                notionInput,
                                githubInput,
                                twilioInput,
                                jiraInput,
                                sshInput
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE SECURE CONFIGS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Instructions banner
        item {
            Text(
                text = "📚 SETUP SECURE GEMINI INTEGRATION",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(1.dp, CyberPanelBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Open the Secrets panel on the left/top sidebar in Google AI Studio.",
                        color = CyberWhite,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2. Add a new Secret with key GEMINI_API_KEY and paste your API key from Google AI Studio.",
                        color = CyberWhite,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "3. The build system will automatically inject this secret into your app's secure buildConfig fields.",
                        color = CyberWhite,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Note: If no API Key is added, the app operates in intelligent local rule-emulation fallback automatically, keeping you fully operational!",
                        color = CyberOrange,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AgentsServerTabContent(viewModel: AgentViewModel) {
    val agents by viewModel.engineeringAgents.collectAsState()
    val cpu by viewModel.serverCpuUsage.collectAsState()
    val ram by viewModel.serverRamUsage.collectAsState()
    val workspace by viewModel.serverActiveWorkspace.collectAsState()
    val connState by viewModel.serverConnectionState.collectAsState()
    val dockerCount by viewModel.serverDockerCount.collectAsState()
    val sshHost by viewModel.sshHostUrl.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Server Connection & Telemetry
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(1.dp, CyberPanelBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = "Server node",
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🖥️ SECURE UBUNTU ENVIRONMENT NODE",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        // Connection State Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        when (connState) {
                                            "CONNECTED" -> CyberGreen
                                            "CONNECTING" -> CyberBlue
                                            else -> CyberRed
                                        },
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = connState,
                                color = when (connState) {
                                    "CONNECTED" -> CyberGreen
                                    "CONNECTING" -> CyberBlue
                                    else -> CyberRed
                                },
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SSH Endpoint and Active workspace
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberBlack)
                            .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "SSH TUNNEL GATEWAY",
                                color = CyberGrayText,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = sshHost,
                                color = CyberWhite,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1.8f), horizontalAlignment = Alignment.End) {
                            Text(
                                text = "ACTIVE SANDBOX DIRECTORY",
                                color = CyberGrayText,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = workspace,
                                color = CyberWhite,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // CPU Metric Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CPU LOAD METRICS (4 CORES)",
                                color = CyberGrayText,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = String.format("%.1f%%", cpu),
                                color = CyberOrange,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (cpu / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyberOrange,
                            trackColor = CyberBlack,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val cpuHistory by viewModel.cpuHistory.collectAsState()
                    TelemetryLineChart(history = cpuHistory)

                    Spacer(modifier = Modifier.height(12.dp))

                    // RAM Metric Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RAM ALLOCATION (DDR5 SECURE ECC)",
                                color = CyberGrayText,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = String.format("%.2f GB / 16.0 GB", ram),
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (ram / 16.0f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyberCyan,
                            trackColor = CyberBlack,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Docker Container count and Run Diagnostic Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(CyberBlack, RoundedCornerShape(4.dp))
                                    .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🐳 DOCKER INSTANCES: ",
                                        color = CyberGrayText,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "$dockerCount LIVE",
                                        color = CyberCyan,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.runRemoteDiagnostics() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "RUN DIAGNOSTICS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Specialized AI Agents List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🤖 SPECIALIZED AI ENGINEERS WORKSPACE",
                    color = CyberWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // List each Specialized Agent
        items(agents) { agent ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(1.dp, when (agent.status) {
                    "WORKING" -> CyberCyan
                    "SUCCESS" -> CyberGreen
                    "FAILED" -> CyberRed
                    else -> CyberPanelBorder
                }),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Title and status badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = agent.name,
                            color = CyberWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .background(
                                    when (agent.status) {
                                        "WORKING" -> CyberCyan.copy(alpha = 0.15f)
                                        "SUCCESS" -> CyberGreen.copy(alpha = 0.15f)
                                        "FAILED" -> CyberRed.copy(alpha = 0.15f)
                                        else -> CyberBlack
                                    },
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        when (agent.status) {
                                            "WORKING" -> CyberCyan
                                            "SUCCESS" -> CyberGreen
                                            "FAILED" -> CyberRed
                                            else -> CyberPanelBorder
                                        }
                                    ),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when (agent.status) {
                                    "WORKING" -> "EXECUTING..."
                                    "SUCCESS" -> "PASSED"
                                    "FAILED" -> "CRITICAL FAIL"
                                    else -> "STANDBY"
                                },
                                color = when (agent.status) {
                                    "WORKING" -> CyberCyan
                                    "SUCCESS" -> CyberGreen
                                    "FAILED" -> CyberRed
                                    else -> CyberGrayText
                                },
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = agent.description,
                        color = CyberGrayText,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Live detail feed
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberBlack)
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "> " + agent.details,
                            color = if (agent.status == "WORKING") CyberCyan else if (agent.status == "SUCCESS") CyberGreen else CyberGrayText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (agent.status == "WORKING") {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { agent.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = CyberCyan,
                            trackColor = CyberBlack
                        )
                    }
                }
            }
        }

        // Tips footer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberCyan.copy(alpha = 0.05f))
                    .border(BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "💡 AGENTIC OS CONTROL CENTER TIP",
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter any software engineering request in the primary Console tab (e.g., 'deploy a python backend microservice to server' or 'create a secure fastapi container').\n\nThe central orchestrator will dynamically connect via secure SSH tunnel to the secure Ubuntu node, and coordinate these 5 specialized agents step-by-step to write, install, verify, test, and containerize your codebase automatically!",
                        color = CyberWhite,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SystemNotificationOverlay(viewModel: AgentViewModel) {
    val notifications by viewModel.inAppNotifications.collectAsState()
    val activeNotification = notifications.firstOrNull { !it.dismissed }

    AnimatedVisibility(
        visible = activeNotification != null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        activeNotification?.let { notif ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notification_banner"),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(2.dp, CyberCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = notif.title,
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = notif.message,
                            color = CyberWhite,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.dismissNotification(notif.id) },
                        modifier = Modifier
                            .size(28.dp)
                            .background(CyberBlack, RoundedCornerShape(4.dp))
                    ) {
                        Text("✕", color = CyberRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuperAgentTabContent(viewModel: AgentViewModel) {
    val orchestrationPlan by viewModel.orchestrationPlan.collectAsState()
    val isOrchestratorActive by viewModel.isOrchestratorActive.collectAsState()
    val currentExecutionTask by viewModel.currentExecutionTask.collectAsState()
    val serverLogs by viewModel.terminalLogs.collectAsState()
    val isProgressTrackerVisible by viewModel.isProgressTrackerVisible.collectAsState()

    var selectedTaskIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Executive Super Agent Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(1.dp, if (isOrchestratorActive) CyberOrange else CyberPanelBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🧠 COGNITIVE SUPER AGENT BRAIN (DAG ORCHESTRATOR)",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        // Active Indicator
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isOrchestratorActive) CyberOrange.copy(alpha = 0.15f) else CyberGreen.copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    BorderStroke(1.dp, if (isOrchestratorActive) CyberOrange else CyberGreen),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isOrchestratorActive) "EXECUTING PIPELINE" else "STANDBY (READY)",
                                color = if (isOrchestratorActive) CyberOrange else CyberGreen,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The Super Agent Orchestration engine automatically decomposes multi-tiered natural language objectives into discrete execution plans. It maps code generation, database schema modeling, unit testing, and Docker compilation to specialized AI agents, resolving failures dynamically via auto-healing loops.",
                        color = CyberGrayText,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isOrchestratorActive) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔄 CURRENT ACTION: $currentExecutionTask",
                                    color = CyberWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                
                                val completedCount = orchestrationPlan.count { it.status == "COMPLETED" }
                                val totalCount = orchestrationPlan.size
                                Text(
                                    text = "$completedCount/$totalCount STAGES",
                                    color = CyberCyan,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val completedCount = orchestrationPlan.count { it.status == "COMPLETED" }
                            val runningCount = orchestrationPlan.count { it.status == "RUNNING" }
                            val totalCount = orchestrationPlan.size.coerceAtLeast(1)
                            val progressFloat = (completedCount.toFloat() + (runningCount.toFloat() * 0.4f)) / totalCount.toFloat()
                            
                            LinearProgressIndicator(
                                progress = { progressFloat.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyberOrange,
                                trackColor = CyberBlack
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.runSuperAgentOrchestrator("Build a healthcare SaaS with React, FastAPI, PostgreSQL, Docker, and deploy it.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("⚡ RUN COGNITIVE PIPELINE DEMO", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // Live Collaboration Dashboard Component
        item {
            LiveCollaborationCockpit(viewModel = viewModel)
        }

        // 1: Step-by-Step Progress Tracker Component
        if (isProgressTrackerVisible) {
            item {
                ExecutionPlanProgressTracker(
                    plan = orchestrationPlan,
                    selectedTaskIndex = selectedTaskIndex,
                    onTaskSelected = { index ->
                        selectedTaskIndex = if (selectedTaskIndex == index) null else index
                    }
                )
            }
        }

        // 2: Scrollable Terminal Output Component
        item {
            UbuntuServerTerminal(
                logs = serverLogs,
                plan = orchestrationPlan,
                onClearLogs = { viewModel.clearLogs() }
            )
        }

        // Execution Plan Pipeline (DAG Timeline UI)
        if (orchestrationPlan.isNotEmpty()) {
            item {
                Text(
                    text = "📋 MULTI-AGENT DAG EXECUTION PIPELINE TIMELINE",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(orchestrationPlan.size) { index ->
                val task = orchestrationPlan[index]
                val isSelected = selectedTaskIndex == index

                val borderHighlight = when (task.status) {
                    "COMPLETED" -> CyberGreen
                    "RUNNING" -> CyberOrange
                    "FAILED" -> CyberRed
                    else -> CyberPanelBorder
                }

                val statusIcon = when (task.status) {
                    "COMPLETED" -> "✅"
                    "RUNNING" -> "⏳"
                    "FAILED" -> "❌"
                    else -> "💤"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTaskIndex = if (isSelected) null else index },
                    colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                    border = BorderStroke(1.dp, borderHighlight),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$statusIcon STAGE ${index + 1}: ${task.title}",
                                    color = CyberWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Agent Badge
                            Box(
                                modifier = Modifier
                                    .background(CyberBlack, RoundedCornerShape(4.dp))
                                    .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = task.assignedAgent,
                                    color = CyberCyan,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = task.description,
                            color = CyberGrayText,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )

                        // If dependency present
                        if (task.dependencies.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🔗 DEPENDENCIES: ",
                                    color = CyberGrayText,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                task.dependencies.forEach { dep ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .background(CyberBlack, RoundedCornerShape(3.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = dep,
                                            color = CyberOrange,
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }

                        // Self-healing custom log insertion for task-6
                        if (task.id == "task-6" && task.status == "FAILED") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CyberRed.copy(alpha = 0.08f))
                                    .border(BorderStroke(1.dp, CyberRed.copy(alpha = 0.4f)), RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "🚨 COMPILATION/BINDING COLLISION BLOCK DETECTED",
                                        color = CyberRed,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "ConnectionError: Port 8000 already in use inside Sandbox Node container network stack.",
                                        color = CyberWhite,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "⚙️ SELF-HEALING ENGINE CORRECTION PIPELINE TRIGGERED:\n-> Modifying FASTAPI_PORT parameter inside environment parameters configuration from 8000 to 8081.\n-> Clearing socket listeners.\n-> Re-executing docker-compose orchestrations...",
                                        color = CyberOrange,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }

                        if (task.status == "RUNNING" && task.progress > 0f) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { task.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.2.dp)),
                                color = CyberOrange,
                                trackColor = CyberBlack
                            )
                        }

                        // Expanded Logs Details
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CyberBlack)
                                    .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = when (task.status) {
                                        "COMPLETED" -> "> Stage compiled successfully.\n> Agent released token lock.\n> Sandbox memory space garbage collected."
                                        "RUNNING" -> "> Deploying secure execution container sandbox...\n> Compiling resources...\n> Executing sub-agents tasks..."
                                        "FAILED" -> "> Port collision check failed.\n> Self-healing active recovery protocol engaged.\n> Overriding port schema to 8081..."
                                        else -> "> Standing by.\n> Awaiting dependent locks release..."
                                    },
                                    color = CyberGrayText,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepositoryTabContent(viewModel: AgentViewModel) {
    val projectFiles by viewModel.projectFiles.collectAsState()
    val gitCommits by viewModel.gitCommits.collectAsState()
    val installedDependencies by viewModel.installedDependencies.collectAsState()
    val envConfig by viewModel.envConfig.collectAsState()

    var selectedFile by remember { mutableStateOf<WorkspaceFile?>(null) }
    
    // Editor State
    var editorContent by remember { mutableStateOf("") }
    var editorFileName by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    // Env Config Input
    var newEnvKey by remember { mutableStateOf("") }
    var newEnvValue by remember { mutableStateOf("") }

    // Default select first file if none selected
    LaunchedEffect(projectFiles) {
        if (selectedFile == null && projectFiles.isNotEmpty()) {
            selectedFile = projectFiles.first()
            editorContent = projectFiles.first().content
            editorFileName = projectFiles.first().name
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main split-screen: Project Explorer & Editor
        item {
            Text(
                text = "📁 PROJECT INTELLIGENCE EXPLORER & CODE EDITOR",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(1.dp, CyberPanelBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Title Header showing active repo
                    Text(
                        text = "REPOSITRY DIRECTORY: /var/www/agentic-os-sandbox",
                        color = CyberGrayText,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Hierarchical collapsible recursive file tree
                    val isIndexing by viewModel.isIndexingRepository.collectAsState()
                    FileTreeNavigator(
                        files = projectFiles,
                        selectedFile = selectedFile,
                        onFileSelected = { file ->
                            selectedFile = file
                            editorContent = file.content
                            editorFileName = file.name
                            isEditing = false
                        },
                        onIndexRequested = { viewModel.indexRepositoryContext() },
                        isIndexing = isIndexing
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Center Code Editor UI
                    selectedFile?.let { file ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberBlack)
                                .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(6.dp))
                                .padding(12.dp)
                        ) {
                            // Editor Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "📄 ${file.path}",
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Size: ${file.size} | Type: ${file.type}",
                                        color = CyberGrayText,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Row {
                                    if (isEditing) {
                                        Button(
                                            onClick = {
                                                viewModel.saveWorkspaceFile(editorFileName, editorContent)
                                                isEditing = false
                                                selectedFile = selectedFile?.copy(content = editorContent)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = CyberBlack),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("SAVE CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Button(
                                            onClick = {
                                                editorContent = file.content
                                                isEditing = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberRed, contentColor = CyberWhite),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("CANCEL", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { isEditing = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("EDIT FILE", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isEditing) {
                                TextField(
                                    value = editorContent,
                                    onValueChange = { editorContent = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    textStyle = TextStyle(
                                        color = CyberWhite,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyberCyan,
                                        unfocusedBorderColor = CyberPanelBorder,
                                        focusedContainerColor = CyberBlack,
                                        unfocusedContainerColor = CyberBlack
                                    )
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = editorContent,
                                        color = CyberGreen,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Environment Variables Configurations (.env) Section
        item {
            Text(
                text = "🛡️ SECURE ENVIRONMENT RUNTIME PARAMETERS (.env)",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(1.dp, CyberPanelBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    envConfig.forEach { (key, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(CyberBlack)
                                .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = key,
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = value,
                                color = CyberWhite,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Add Env Variable Form
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newEnvKey,
                            onValueChange = { newEnvKey = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("NEW_KEY", fontSize = 10.sp, color = CyberGrayText) },
                            singleLine = true,
                            textStyle = TextStyle(color = CyberWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberPanelBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = newEnvValue,
                            onValueChange = { newEnvValue = it },
                            modifier = Modifier.weight(1.2f),
                            placeholder = { Text("value_parameter", fontSize = 10.sp, color = CyberGrayText) },
                            singleLine = true,
                            textStyle = TextStyle(color = CyberWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberPanelBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (newEnvKey.isNotBlank() && newEnvValue.isNotBlank()) {
                                    viewModel.updateEnvConfig(newEnvKey.uppercase().trim(), newEnvValue.trim())
                                    newEnvKey = ""
                                    newEnvValue = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("ADD", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Timeline columns: Git and Dependencies
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🌿 GIT REPOSITORY HISTORY",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "📦 PACKAGES",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Git list
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberDarkPanel, RoundedCornerShape(8.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    gitCommits.forEach { commit ->
                        Text(
                            text = commit,
                            color = CyberWhite,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 13.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(CyberPanelBorder.copy(alpha = 0.5f))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Dependencies list
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberDarkPanel, RoundedCornerShape(8.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    installedDependencies.forEach { (name, ver) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = name,
                                color = CyberCyan,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ver,
                                color = CyberWhite,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(CyberPanelBorder.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryLineChart(history: List<Float>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(CyberBlack, RoundedCornerShape(6.dp))
            .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw horizontal grid lines
            val gridColor = CyberPanelBorder.copy(alpha = 0.3f)
            val gridRows = 4
            for (i in 0..gridRows) {
                val y = i * (height / gridRows)
                drawLine(
                    color = gridColor,
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // 2. Draw vertical grid lines
            val gridCols = 8
            for (i in 0..gridCols) {
                val x = i * (width / gridCols)
                drawLine(
                    color = gridColor,
                    start = androidx.compose.ui.geometry.Offset(x, 0f),
                    end = androidx.compose.ui.geometry.Offset(x, height),
                    strokeWidth = 1f
                )
            }

            // 3. Draw rolling telemetry line
            if (history.size > 1) {
                val stepX = width / (history.size - 1)
                val path = Path()
                
                history.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = height - ((value / 100f) * height).coerceIn(0f, height)
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                // Draw path with thickness & glowing cyan color
                drawPath(
                    path = path,
                    color = CyberCyan,
                    style = Stroke(
                        width = 4f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Fill area under line with soft semi-transparent gradient
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(CyberCyan.copy(alpha = 0.2f), Color.Transparent)
                    )
                )

                // Draw glowing node at the latest point
                val lastVal = history.last()
                val lastX = width
                val lastY = height - ((lastVal / 100f) * height).coerceIn(0f, height)
                drawCircle(
                    color = CyberCyan,
                    radius = 6f,
                    center = androidx.compose.ui.geometry.Offset(lastX, lastY)
                )
                drawCircle(
                    color = CyberCyan.copy(alpha = 0.4f),
                    radius = 12f,
                    center = androidx.compose.ui.geometry.Offset(lastX, lastY)
                )
            }
        }

        // Overlay text labels
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "T-40s",
                color = CyberGrayText,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
            Text(
                text = "REAL-TIME CPU HISTORY MONITOR",
                color = CyberCyan.copy(alpha = 0.7f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = "NOW",
                color = CyberCyan,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
            )
        }
    }
}

@Composable
fun FileTreeNavigator(
    files: List<WorkspaceFile>,
    selectedFile: WorkspaceFile?,
    onFileSelected: (WorkspaceFile) -> Unit,
    onIndexRequested: () -> Unit,
    isIndexing: Boolean
) {
    // Keep track of which directories are expanded (default to expanded)
    val expandedFolders = remember { androidx.compose.runtime.mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberBlack, RoundedCornerShape(6.dp))
            .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📁 PROJECT DIRECTORY & TREE INDEX",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            
            // Indexing action button
            Button(
                onClick = onIndexRequested,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isIndexing) CyberOrange.copy(alpha = 0.2f) else CyberCyan.copy(alpha = 0.15f),
                    contentColor = if (isIndexing) CyberOrange else CyberCyan
                ),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, if (isIndexing) CyberOrange else CyberCyan),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(24.dp)
            ) {
                Text(
                    text = if (isIndexing) "INDEXING..." else "INDEX REPOSITORY",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Group files by directories
        val filesByFolder = remember(files) {
            val map = mutableMapOf<String, MutableList<WorkspaceFile>>()
            files.forEach { file ->
                val pathParts = file.path.trimStart('/').split('/')
                if (pathParts.size > 1) {
                    val folderPath = "/" + pathParts.dropLast(1).joinToString("/")
                    map.getOrPut(folderPath) { mutableListOf() }.add(file)
                } else {
                    map.getOrPut("/") { mutableListOf() }.add(file)
                }
            }
            map.toSortedMap()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp)
                .verticalScroll(rememberScrollState())
        ) {
            filesByFolder.forEach { (folder, folderFiles) ->
                val isRoot = folder == "/"
                val isExpanded = expandedFolders.getOrPut(folder) { true }

                if (!isRoot) {
                    // Render folder row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedFolders[folder] = !isExpanded }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "▼" else "▶",
                            color = CyberCyan,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.width(14.dp)
                        )
                        Text(
                            text = "📁",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = folder.substringAfterLast("/"),
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isRoot || isExpanded) {
                    // Render files in this folder
                    folderFiles.forEach { file ->
                        val isCurrent = selectedFile?.path == file.path
                        val indent = if (isRoot) 0.dp else 16.dp
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = indent)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isCurrent) CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { onFileSelected(file) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📄",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = file.name,
                                color = if (isCurrent) CyberCyan else CyberWhite,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = file.size,
                                color = CyberGrayText,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExecutionPlanProgressTracker(
    plan: List<OrchestrationSubtask>,
    selectedTaskIndex: Int?,
    onTaskSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
        border = BorderStroke(1.dp, CyberPanelBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "🧠 COGNITIVE PIPELINE EXECUTION STEP-BY-STEP PROGRESS",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable row of stages
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // If plan is empty, draw 7 pending placeholders
                val displayPlan = if (plan.isNotEmpty()) plan else List(7) { index ->
                    OrchestrationSubtask(
                        id = "task-${index + 1}",
                        title = "Stage ${index + 1}",
                        description = "",
                        assignedAgent = "",
                        status = "PENDING",
                        dependencies = emptyList(),
                        progress = 0f
                    )
                }

                val conciseLabels = listOf(
                    "Parse Req",
                    "DB Schema",
                    "Backend",
                    "Frontend",
                    "DevOps",
                    "Testing",
                    "Deploy"
                )

                displayPlan.forEachIndexed { index, task ->
                    val isSelected = selectedTaskIndex == index
                    val status = task.status
                    val label = conciseLabels.getOrElse(index) { "Stage ${index + 1}" }

                    // Color and icon coding for this node
                    val (statusColor, statusIcon, iconTint, isPulsing) = when (status) {
                        "COMPLETED" -> CyberQuadruple(CyberGreen, "✔", CyberWhite, false)
                        "RUNNING" -> CyberQuadruple(CyberOrange, "⏳", CyberWhite, true)
                        "FAILED" -> CyberQuadruple(CyberRed, "🚨", CyberWhite, false)
                        else -> CyberQuadruple(CyberPanelBorder, (index + 1).toString(), CyberGrayText, false)
                    }

                    // Render Node Circle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onTaskSelected(index) }
                            .padding(horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) statusColor.copy(alpha = 0.2f) else CyberBlack)
                                .border(
                                    BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) CyberCyan else statusColor
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPulsing) {
                                PulsingStatusCircle(color = CyberOrange)
                            }
                            Text(
                                text = statusIcon,
                                color = if (isSelected) CyberCyan else iconTint,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            color = if (isSelected) CyberCyan else if (status == "COMPLETED") CyberWhite else CyberGrayText,
                            fontSize = 8.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                        Text(
                            text = status,
                            color = if (status == "RUNNING") CyberOrange else if (status == "COMPLETED") CyberGreen else if (status == "FAILED") CyberRed else CyberGrayText.copy(alpha = 0.6f),
                            fontSize = 7.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Connector line to the next node (except last)
                    if (index < displayPlan.size - 1) {
                        val nextStatus = displayPlan[index + 1].status
                        val lineColor = when {
                            status == "COMPLETED" && (nextStatus == "COMPLETED" || nextStatus == "RUNNING") -> CyberGreen
                            status == "COMPLETED" -> CyberCyan
                            status == "RUNNING" -> CyberOrange
                            else -> CyberPanelBorder.copy(alpha = 0.4f)
                        }

                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(if (status == "PENDING") 1.dp else 2.dp)
                                .background(lineColor)
                                .padding(horizontal = 2.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

data class CyberQuadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun PulsingStatusCircle(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .border(BorderStroke(1.dp, color), CircleShape)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UbuntuServerTerminal(
    logs: List<String>,
    plan: List<OrchestrationSubtask>,
    onClearLogs: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll on logs size update
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(logs.size - 1)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
        border = BorderStroke(1.dp, CyberPanelBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💻 UBUNTU@AGENTIC-OS-SERVER:~ (SSH STREAM)",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // Action controls
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(CyberGreen.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .border(BorderStroke(1.dp, CyberGreen.copy(alpha = 0.4f)), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(CyberGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "TUNNEL LIVE",
                                color = CyberGreen,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Refresh/Clear log button
                    IconButton(
                        onClick = onClearLogs,
                        modifier = Modifier
                            .size(18.dp)
                            .background(CyberBlack, RoundedCornerShape(3.dp))
                    ) {
                        Text("✕", color = CyberRed, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-Agent Status Monitors Section
            Text(
                text = "⚡ COGNITIVE AGENTS ACTIVE STATUS DIRECTORY",
                color = CyberGrayText,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Flow row of agents statuses
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                maxItemsInEachRow = 4
            ) {
                val agents = listOf(
                    "Requirement Analyzer" to "task-1",
                    "Database Architect" to "task-2",
                    "Backend Developer" to "task-3",
                    "Frontend Engineer" to "task-4",
                    "DevOps Specialist" to "task-5",
                    "Test Suite Runner" to "task-6",
                    "Deployment Specialist" to "task-7"
                )

                agents.forEach { (agentName, taskId) ->
                    val task = plan.find { it.id == taskId }
                    val status = task?.status ?: "PENDING"
                    
                    val (dotColor, badgeBorder, badgeBg, textColor) = when (status) {
                        "COMPLETED" -> CyberQuadruple(CyberGreen, CyberGreen.copy(alpha = 0.4f), CyberBlack, CyberWhite)
                        "RUNNING" -> CyberQuadruple(CyberOrange, CyberOrange, CyberOrange.copy(alpha = 0.08f), CyberWhite)
                        "FAILED" -> CyberQuadruple(CyberRed, CyberRed, CyberRed.copy(alpha = 0.1f), CyberRed)
                        else -> CyberQuadruple(CyberGrayText, CyberPanelBorder, CyberBlack, CyberGrayText)
                    }

                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(4.dp))
                            .border(BorderStroke(1.dp, badgeBorder), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (status == "RUNNING") {
                                PulsingStatusDot(color = CyberOrange)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(dotColor, CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = agentName.uppercase(),
                                color = textColor,
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // The main interactive highlight terminal window
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(CyberBlack, RoundedCornerShape(6.dp))
                    .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AWAITING REMOTE SERVER ORCHESTRATION PIPELINE TRANSMISSIONS...\nPRESS 'RUN COGNITIVE PIPELINE DEMO' TO STREAM LOGS",
                            color = CyberGrayText.copy(alpha = 0.6f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(logs) { log ->
                            TerminalHighlightedLine(log)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PulsingStatusDot(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(6.dp)
            .alpha(alpha)
            .background(color, CircleShape)
    )
}

@Composable
fun TerminalHighlightedLine(log: String) {
    val trimmed = log.trim()
    
    // Determine style based on server output categories
    val (lineColor, fontStyle, prefix) = when {
        // Prompts and commands
        trimmed.startsWith("$") || trimmed.startsWith("root@") || trimmed.contains("Executing:") -> 
            Triple(CyberCyan, FontFamily.Monospace, "➜ ")
            
        // Success indications
        trimmed.contains("✅") || trimmed.contains("success") || trimmed.contains("passed") || trimmed.contains("SUCCESS") || trimmed.contains("Successfully") -> 
            Triple(CyberGreen, FontFamily.Monospace, "")
            
        // Errors/Failures
        trimmed.contains("❌") || trimmed.contains("FAILED") || trimmed.contains("test fail") || trimmed.contains("TEST FAIL") || trimmed.contains("ConnectionError") || trimmed.contains("🚨") || trimmed.contains("error") -> 
            Triple(CyberRed, FontFamily.Monospace, "")
            
        // Warning & Auto healing states
        trimmed.contains("⚠️") || trimmed.contains("warning") || trimmed.contains("Auto-Healing") || trimmed.contains("retrying") || trimmed.contains("🔧") || trimmed.contains("🔄") -> 
            Triple(CyberOrange, FontFamily.Monospace, "")
            
        // Sub-agent identifiers
        trimmed.contains("[Requirement Analyzer]") || trimmed.contains("[Database Architect]") || trimmed.contains("[Backend Developer]") || trimmed.contains("[Frontend Engineer]") || trimmed.contains("[DevOps Specialist]") || trimmed.contains("[Test Suite Runner]") || trimmed.contains("[Deployment Specialist]") || trimmed.contains("[Super Agent Brain]") -> 
            Triple(CyberCyan, FontFamily.Monospace, "")
            
        // Directories tree logs
        trimmed.startsWith("   ├──") || trimmed.startsWith("   └──") || trimmed.startsWith("  ") -> 
            Triple(CyberGrayText, FontFamily.Monospace, "")
            
        else -> 
            Triple(CyberWhite, FontFamily.Monospace, "")
    }

    Text(
        text = "$prefix$log",
        color = lineColor,
        fontSize = 10.sp,
        fontFamily = fontStyle,
        lineHeight = 14.sp
    )
}

@Composable
fun LiveCollaborationCockpit(viewModel: AgentViewModel) {
    val isOrchestratorActive by viewModel.isOrchestratorActive.collectAsState()
    val currentExecutionTask by viewModel.currentExecutionTask.collectAsState()
    val agents by viewModel.engineeringAgents.collectAsState()
    val cpuUsage by viewModel.serverCpuUsage.collectAsState()
    val ramUsage by viewModel.serverRamUsage.collectAsState()
    val dockerCount by viewModel.serverDockerCount.collectAsState()
    val sshHost by viewModel.sshHostUrl.collectAsState()
    val connectionState by viewModel.serverConnectionState.collectAsState()

    // Determine current pipeline step or info
    val activeTask = if (isOrchestratorActive) currentExecutionTask else "STANDBY (READY TO ORCHESTRATE)"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
        border = BorderStroke(1.dp, if (isOrchestratorActive) CyberCyan else CyberPanelBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (isOrchestratorActive) CyberGreen else CyberCyan, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🤝 COGNITIVE AGENTS LIVE COLLABORATION HUB",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(CyberBlack, RoundedCornerShape(4.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "REAL-TIME STREAM",
                        color = if (isOrchestratorActive) CyberGreen else CyberGrayText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Current Task Panel
            Text(
                text = "📍 CURRENT ACTIVE COGNITIVE TASK",
                color = CyberGrayText,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberBlack, RoundedCornerShape(4.dp))
                    .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isOrchestratorActive) {
                    PulsingStatusDot(color = CyberOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = activeTask.uppercase(),
                    color = if (isOrchestratorActive) CyberWhite else CyberGrayText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Active Agents status horizontal grid
            Text(
                text = "👥 SPECIALIZED WORKFORCE DISPATCH DIRECTORY",
                color = CyberGrayText,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                agents.forEach { agent ->
                    val isActive = agent.status == "WORKING"
                    val isSuccess = agent.status == "SUCCESS"
                    val isFailed = agent.status == "FAILED"

                    val (textColor, statusLabel) = when {
                        isActive -> Pair(CyberOrange, "WORKING")
                        isSuccess -> Pair(CyberGreen, "SUCCESS")
                        isFailed -> Pair(CyberRed, "FAILED")
                        else -> Pair(CyberGrayText, "IDLE")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberBlack, RoundedCornerShape(4.dp))
                            .border(BorderStroke(1.dp, if (isActive) CyberOrange.copy(alpha = 0.5f) else CyberPanelBorder), RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = agent.name,
                                color = CyberWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = agent.details,
                                color = CyberGrayText,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isActive) {
                                PulsingStatusDot(color = CyberOrange)
                            }
                            Text(
                                text = statusLabel,
                                color = textColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Resource Usage Section (Gauges / Progress bars)
            Text(
                text = "📊 UBUNTU INSTANCE HARDWARE RESOURCE USAGE",
                color = CyberGrayText,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // CPU Progress
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberBlack, RoundedCornerShape(4.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("CPU", color = CyberGrayText, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text("${String.format("%.1f", cpuUsage)}%", color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { cpuUsage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = CyberCyan,
                        trackColor = CyberPanelBorder
                    )
                }

                // RAM Progress
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberBlack, RoundedCornerShape(4.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("RAM", color = CyberGrayText, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text("${String.format("%.2f", ramUsage)} GB", color = CyberPurple, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (ramUsage / 16f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = CyberPurple,
                        trackColor = CyberPanelBorder
                    )
                }

                // Docker Active Containers Count
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .background(CyberBlack, RoundedCornerShape(4.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("DOCKER SANDBOXES", color = CyberGrayText, fontSize = 7.5.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$dockerCount ACTIVE",
                        color = CyberGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. File Changes (Live File Write Alerts)
            Text(
                text = "📂 CONCURRENT FILE GENERATION FEED (REAL-TIME WRITES)",
                color = CyberGrayText,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(CyberBlack, RoundedCornerShape(4.dp))
                    .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                val mockFiles = listOf(
                    Triple("main.py", "FastAPI Application Core", "CREATED"),
                    Triple("schema.sql", "PostgreSQL DDL Schemas", "MODIFIED"),
                    Triple("requirements.txt", "Vulnerability Audited Dependencies", "CREATED"),
                    Triple("Dockerfile", "Alpine Multi-Stage Container Setup", "CREATED"),
                    Triple("docker-compose.yml", "Secure Network Bridge Topology", "CREATED")
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(mockFiles) { (name, desc, status) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "📄 $name",
                                    color = CyberCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "- $desc",
                                    color = CyberGrayText,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        if (status == "CREATED") CyberGreen.copy(alpha = 0.15f) else CyberOrange.copy(alpha = 0.15f),
                                        RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = status,
                                    color = if (status == "CREATED") CyberGreen else CyberOrange,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Test Results Suite Panel & Deployment Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Test Suite status
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberBlack, RoundedCornerShape(4.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "🧪 ACTIVE VERIFICATION SUITE",
                        color = CyberGrayText,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val isTesting = activeTask.contains("Test") || activeTask.contains("Verify")
                    val hasCompleted = cpuUsage == 8.5f && !isOrchestratorActive && dockerCount >= 1

                    Text(
                        text = if (isTesting) "RUNNING PYTEST..." else if (hasCompleted) "12/12 PASSED (100%)" else "AWAITING VERIFICATION",
                        color = if (isTesting) CyberOrange else if (hasCompleted) CyberGreen else CyberGrayText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (hasCompleted) "Coverage: 95.8% (OK)" else "Lint checks: PENDING",
                        color = if (hasCompleted) CyberGreen.copy(alpha = 0.8f) else CyberGrayText.copy(alpha = 0.6f),
                        fontSize = 7.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Deployment status
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .background(CyberBlack, RoundedCornerShape(4.dp))
                        .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "🌍 SECURE EXPOSED DEPLOYMENT",
                        color = CyberGrayText,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val deployed = cpuUsage == 8.5f && !isOrchestratorActive && dockerCount >= 1

                    Text(
                        text = if (deployed) "STATUS: TUNNEL LIVE" else if (isOrchestratorActive) "DEPLOYING CONTAINER..." else "STANDBY (READY)",
                        color = if (deployed) CyberGreen else if (isOrchestratorActive) CyberOrange else CyberGrayText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (deployed) "http://$sshHost:8081/health" else "Endpoint binding: PENDING",
                        color = if (deployed) CyberCyan else CyberGrayText.copy(alpha = 0.6f),
                        fontSize = 7.5.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityApprovalModal(viewModel: AgentViewModel) {
    val pendingApproval by viewModel.pendingApproval.collectAsState()

    pendingApproval?.let { approval ->
        val borderAndAccentColor = if (approval.riskLevel == "CRITICAL") CyberRed else CyberOrange
        val riskLabel = if (approval.riskLevel == "CRITICAL") "🚨 CRITICAL DESTRUCTIVE ACTION" else "⚠️ HIGH RISK AUTOMATED ACTION"

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBlack.copy(alpha = 0.9f))
                .clickable(enabled = true, onClick = {}) 
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = CyberDarkPanel),
                border = BorderStroke(2.dp, borderAndAccentColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(borderAndAccentColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "SECURITY GATE AUTHORIZATION",
                            color = borderAndAccentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .background(borderAndAccentColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(BorderStroke(1.dp, borderAndAccentColor), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = riskLabel,
                            color = borderAndAccentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = approval.title,
                        color = CyberWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = approval.description,
                        color = CyberGrayText,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "🎯 TARGET AFFECTED ASSETS:",
                        color = CyberCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberBlack, RoundedCornerShape(4.dp))
                            .border(BorderStroke(1.dp, CyberPanelBorder), RoundedCornerShape(4.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        approval.affectedAssets.forEach { asset ->
                            Text(
                                text = "• $asset",
                                color = CyberWhite,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setApprovalDecision(false) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberRed, contentColor = CyberWhite),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "✕ DENY & ABORT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = { viewModel.setApprovalDecision(true) },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = borderAndAccentColor, contentColor = CyberBlack),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "🔓 AUTHORIZE OPERATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShortcutBadge(key: String, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(CyberCyan.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
            .border(BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)), RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$key ",
                color = CyberWhite,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = desc,
                color = CyberGrayText,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

