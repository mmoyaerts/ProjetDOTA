<template>
  <div class="recent-matches">
    <h2>Matchs récents (live)</h2>
    <pre class="output">{{ output }}</pre>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const output = ref('')
const idTest = '365939353'
const nomProduceur = 'producteurRecentMatch.py'
onMounted(() => {
  const socket = new WebSocket(
    `ws://localhost:8080/ws/match-stream?script=${nomProduceur}&accountId=${idTest}`
  )

  socket.onopen = () => console.log('✅ WS connecté')
  socket.onmessage = e => { output.value += e.data + '\n' }
  socket.onerror   = err => console.error('❌ WS erreur', err)
  socket.onclose   = () => console.log('❎ WS fermé')
})
</script>

<style scoped>
.recent-matches {
  margin-top: 2rem;
}
.output {
  white-space: pre-wrap;
  background: #f3f4f6;
  padding: 1rem;
  border-radius: 4px;
  max-height: 300px;
  overflow: auto;
}
</style>
