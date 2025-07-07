<template>
  <div>
    <h2>WebSocket Output</h2>
    <div id="output">{{ output }}</div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

const output = ref('')
const idTest = '365939353'
const nomProduceur = 'producteurRecentMatch.py'

// WebSocket initialisation
onMounted(() => {
  const socket = new WebSocket(`ws://localhost:8080/ws/match-stream?script=${nomProduceur}&accountId=${idTest}`)

  socket.onopen = () => {
    console.log("✅ WebSocket connecté")
  }

  socket.onmessage = (event) => {
    output.value += event.data + '\n'
  }

  socket.onerror = (error) => {
    console.error("❌ WebSocket erreur", error)
  }

  socket.onclose = () => {
    console.log("❎ WebSocket fermé")
  }
})
</script>

<style scoped>
#output {
  white-space: pre-wrap;
  background-color: #f0f0f0;
  padding: 10px;
}
</style>
