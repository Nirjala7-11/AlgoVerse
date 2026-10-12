package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CustomArrayInput(
  rawInput: String,
  onInputChange: (String) -> Unit,
  onApply: () -> Unit,
  errorMessage: String?,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = rawInput,
        onValueChange = onInputChange,
        modifier = Modifier
          .weight(1f)
          .testTag("custom_array_input_field"),
        label = { Text("Custom Student Array (comma-separated, max 30)") },
        placeholder = { Text("e.g. 24, 7, 36, 12, 5, 19, 42, 1") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.EditNote,
            contentDescription = "Array input"
          )
        },
        trailingIcon = {
          if (rawInput.isNotEmpty()) {
            IconButton(
              onClick = { onInputChange("") },
              modifier = Modifier.testTag("clear_array_input_button")
            ) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear input"
              )
            }
          }
        },
        isError = errorMessage != null,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
          onDone = {
            focusManager.clearFocus()
            onApply()
          }
        ),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
      )

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = {
          focusManager.clearFocus()
          onApply()
        },
        modifier = Modifier
          .height(56.dp)
          .testTag("apply_array_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          modifier = Modifier.padding(end = 4.dp)
        )
        Text("Apply")
      }
    }

    if (errorMessage != null) {
      Spacer(modifier = Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.ErrorOutline,
          contentDescription = "Error",
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(end = 4.dp)
        )
        Text(
          text = errorMessage,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall
        )
      }
    }
  }
}
