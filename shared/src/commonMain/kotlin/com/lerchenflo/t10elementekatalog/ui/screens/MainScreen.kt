package com.lerchenflo.t10elementekatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lerchenflo.t10elementekatalog.model.Element

@Composable
fun MainScreen(
    elements: List<Element>,
    onCreateElement: () -> Unit,
    onOpenPointCounter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("T10 Elementekatalog") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCreateElement,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Element erstellen")
            }
            
            Button(
                onClick = onOpenPointCounter,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Punktezähler öffnen")
            }
            
            ElementList(
                elements = elements,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ElementList(
    elements: List<Element>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(elements) { element ->
            ElementCard(element = element)
        }
    }
}

@Composable
private fun ElementCard(
    element: Element,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = element.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = element.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${element.getDifficultyName()} | ${element.getElementTypeName()}")
                Text("${element.getCategoryName()} | ${element.points} Punkte")
            }
        }
    }
} 