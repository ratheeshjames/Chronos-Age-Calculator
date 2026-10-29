package com.example.ui.profiles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Profile
import com.example.data.Relationship
import com.example.ui.theme.CardExpressiveShape
import com.example.ui.theme.ChipExpressiveShape
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState
import java.time.LocalDate
import java.time.Period

@Composable
fun ProfileRegistryScreen(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  listState: LazyListState,
  modifier: Modifier = Modifier
) {
  var searchQuery by rememberSaveable { mutableStateOf("") }
  var selectedRelationshipFilterName by rememberSaveable { mutableStateOf<String?>(null) }
  var pendingDeleteProfileId by rememberSaveable { mutableStateOf<String?>(null) }

  val selectedRelationshipFilter = remember(selectedRelationshipFilterName) {
    Relationship.entries.find { it.name == selectedRelationshipFilterName }
  }
  val profilePendingDelete = remember(uiState.profiles, pendingDeleteProfileId) {
    uiState.profiles.find { it.id == pendingDeleteProfileId }
  }

  val filteredProfiles by remember(uiState.profiles, searchQuery, selectedRelationshipFilter) {
    derivedStateOf {
      uiState.profiles.filter { profile ->
        val matchesQuery = profile.name.contains(searchQuery, ignoreCase = true)
        val matchesRel = selectedRelationshipFilter == null || profile.relationship == selectedRelationshipFilter
        matchesQuery && matchesRel
      }
    }
  }

  LazyColumn(
    state = listState,
    modifier = modifier
      .fillMaxSize()
      .imePadding()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Search & Filter Bar
    item(key = "search_bar") {
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by profile name...", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_profiles_input"),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
      )
    }

    // Relationship Chips
    item(key = "relationship_chips") {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedRelationshipFilter == null,
          onClick = { selectedRelationshipFilterName = null },
          label = { Text("All (${uiState.profiles.size})", maxLines = 1, overflow = TextOverflow.Ellipsis) },
          shape = ChipExpressiveShape
        )
        Relationship.entries.forEach { rel ->
          val count = uiState.profiles.count { it.relationship == rel }
          if (count > 0) {
            FilterChip(
              selected = selectedRelationshipFilter == rel,
              onClick = {
                selectedRelationshipFilterName = if (selectedRelationshipFilter == rel) null else rel.name
              },
              label = { Text("${rel.label} ($count)", maxLines = 1, overflow = TextOverflow.Ellipsis) },
              shape = ChipExpressiveShape
            )
          }
        }
      }
    }

    // Profiles List with stable keys
    items(
      items = filteredProfiles,
      key = { it.id }
    ) { profile ->
      val isSelected = profile.id == uiState.activeProfile?.id
      val ageYears = Period.between(profile.birthDate, LocalDate.now()).years

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onIntent(ChronosUiIntent.SelectActiveProfile(profile.id)) }
          .testTag("profile_card_${profile.id}"),
        shape = CardExpressiveShape,
        colors = CardDefaults.cardColors(
          containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(Color(profile.avatarColorHex)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = profile.name.take(1).uppercase(),
                  color = Color.White,
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Bold
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                  )
                  if (isSelected) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = "ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                      )
                    }
                  }
                }

                Text(
                  text = "${profile.relationship.label} • Age $ageYears",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "Born ${profile.birthDate}" + (profile.birthTime?.let { " at $it" } ?: ""),
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.outline,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            // Star Primary Toggle
            IconButton(
              onClick = { onIntent(ChronosUiIntent.SetPrimaryProfile(profile.id)) },
              modifier = Modifier.testTag("star_profile_${profile.id}")
            ) {
              Icon(
                imageVector = if (profile.isPrimary) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Primary",
                tint = if (profile.isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Card Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Duplicate
            TextButton(
              onClick = {
                onIntent(
                  ChronosUiIntent.SaveProfile(
                    id = null,
                    name = "${profile.name} (Copy)",
                    birthDate = profile.birthDate,
                    birthTime = profile.birthTime,
                    relationship = profile.relationship,
                    colorHex = profile.avatarColorHex,
                    isPrimary = false
                  )
                )
              }
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Copy")
            }

            // Edit
            TextButton(
              onClick = { onIntent(ChronosUiIntent.OpenEditProfileSheet(profile)) }
            ) {
              Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Edit")
            }

            // Delete (only if > 1 profile)
            if (uiState.profiles.size > 1) {
              TextButton(
                onClick = { pendingDeleteProfileId = profile.id },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
              ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete", maxLines = 1, overflow = TextOverflow.Ellipsis)
              }
            }
          }
        }
      }
    }

    // Load Famous Historical Icons Catalog (Feature 27)
    item(key = "historical_catalog") {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.HistoryEdu, contentDescription = "History", tint = MaterialTheme.colorScheme.primary)
            Text(
              text = "Explore Historical Icons",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Compare your timeline against Albert Einstein, Ada Lovelace, Leonardo da Vinci, and Marie Curie.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedButton(
            onClick = {
              val historicalFigures = listOf(
                Profile(
                  id = "hist_vinci",
                  name = "Leonardo da Vinci",
                  birthDate = LocalDate.of(1452, 4, 15),
                  relationship = Relationship.HISTORIC,
                  avatarColorHex = 0xFFDDA15E
                ),
                Profile(
                  id = "hist_curie",
                  name = "Marie Curie",
                  birthDate = LocalDate.of(1867, 11, 7),
                  relationship = Relationship.HISTORIC,
                  avatarColorHex = 0xFF8338EC
                )
              )
              historicalFigures.forEach { hist ->
                if (uiState.profiles.none { it.name == hist.name }) {
                  onIntent(
                    ChronosUiIntent.SaveProfile(
                      id = null,
                      name = hist.name,
                      birthDate = hist.birthDate,
                      birthTime = hist.birthTime,
                      relationship = hist.relationship,
                      colorHex = hist.avatarColorHex,
                      isPrimary = false
                    )
                  )
                }
              }
              onIntent(ChronosUiIntent.ShowToast("Historical profiles loaded!"))
            },
            modifier = Modifier
              .fillMaxWidth()
              .defaultMinSize(minHeight = 48.dp)
          ) {
            Text(
              text = "Inject Da Vinci & Marie Curie",
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    item(key = "bottom_spacer") {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Deletion Confirmation Dialog
  profilePendingDelete?.let { pending ->
    AlertDialog(
      onDismissRequest = { pendingDeleteProfileId = null },
      title = { Text("Delete Profile") },
      text = { Text("Are you sure you want to delete '${pending.name}'? All chronometric history will be removed.") },
      confirmButton = {
        Button(
          onClick = {
            onIntent(ChronosUiIntent.DeleteProfile(pending.id))
            pendingDeleteProfileId = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { pendingDeleteProfileId = null }) {
          Text("Cancel")
        }
      }
    )
  }
}
