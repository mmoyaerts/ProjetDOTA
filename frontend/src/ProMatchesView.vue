<template>
  <div class="container">
    <section class="recent-section">
      <div class="table-wrapper" v-if="recentMatches.length">
        <table class="recent-table">
          <thead>
            <tr>
              <th>Héros</th>
              <th>K</th>
              <th>D</th>
              <th>A</th>
              <th>Durée</th>
              <th>Équipe</th>
              <th>Résultat</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="m in recentMatches"
              :key="m.match_id"
              :class="m.victory ? 'victory-row' : 'defeat-row'"
            >
              <td class="hero-cell">
                <img
                  :src="`/heroes/${m.hero_id}.png`"
                  :alt="m.heroName"
                  class="hero-icon"
                />
                <span class="hero-name">{{ m.heroName }}</span>
              </td>
              <td>{{ m.kills }}</td>
              <td>{{ m.deaths }}</td>
              <td>{{ m.assists }}</td>
              <td>{{ formatDuration(m.duration) }}m</td>
              <td>{{ m.isRadian ? 'Radiant' : 'Dire' }}</td>
              <td>{{ m.victory ? 'Victoire' : 'Défaite' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-else class="no-games">Aucun match récent pour ce joueur.</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const playerId = route.params.id

const recentMatches = ref([])
let socket = null

onMounted(async () => {
  socket = new WebSocket(
    `ws://localhost:8080/ws/match-stream?script=producteurRecentMatch.py&accountId=${playerId}`
  )
  socket.onmessage = async e => {
    let data
    try {
      data = JSON.parse(e.data)
    } catch {
      return
    }
    const withNames = await Promise.all(
      data.map(async m => {
        let name = `Héros ${m.hero_id}`
        try {
          const res = await axios.get(`/heroes/${m.hero_id}`)
          name = res.data.name
        } catch {}
        return { ...m, heroName: name }
      })
    )
    recentMatches.value = withNames
  }
})

onBeforeUnmount(() => {
  socket && socket.close()
})

function formatDuration(sec) {
  return Math.floor(sec / 60)
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
  color: #111;
}

/* wrapper défilable avec arrondis + ombre */
.table-wrapper {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  max-height: 600px;
  overflow-y: auto;
  overflow-x: auto;
}

/* tableau */
.recent-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 15px;
}

/* header sticky */
.recent-table thead th {
  position: sticky;
  top: 0;
  background: #f5f5f5;
  z-index: 2;
  padding: 0.75rem 1rem;
  text-align: left;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  color: #555;
}

/* coins arrondis du header */
.table-wrapper table thead th:first-child {
  border-top-left-radius: 8px;
}
.table-wrapper table thead th:last-child {
  border-top-right-radius: 8px;
}

/* lignes */
.recent-table tbody tr {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.recent-table td {
  border: none;
  padding: 1rem;
  text-align: center;
  vertical-align: middle;
}

/* cellule héros */
.hero-cell {
  display: flex;
  align-items: center;
  gap: 1rem;
  text-align: left;
}
.hero-icon {
  width: 72px;
  height: 72px;
  object-fit: contain;
  border-radius: 4px;
  background: #f9f9f9;
  border: 1px solid #eee;
}
.hero-name {
  font-weight: bold;
  font-size: 1.1rem;
}

/* victoire / défaite */
.victory-row td { background-color: #e6ffed; }
.defeat-row td  { background-color: #ffeaea; }

/* message vide */
.no-games {
  text-align: center;
  color: #888;
  font-style: italic;
  margin-top: 2rem;
}
</style>
