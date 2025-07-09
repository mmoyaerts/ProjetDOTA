<template>
  <div class="container">
    <!-- Titre dynamique avec uniquement le pseudo -->
    <h1 class="title">Historique du pro {{ proName }}</h1>

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

const route        = useRoute()
const playerId     = route.params.id
const token        = localStorage.getItem('jwt_token') || ''

const proName      = ref('Chargement…')
const recentMatches = ref([])

let socket = null

onMounted(async () => {
  // 1) Charger le pseudo du pro
  try {
    const res = await axios.get(
      `/pros/${playerId}`,
      {
        headers: { Authorization: `Bearer ${token}` },
        withCredentials: true
      }
    )
    proName.value = res.data.pseudo
  } catch (err) {
    console.error('❌ Impossible de récupérer le pro :', err)
    proName.value = `#${playerId}`
  }

  // 2) Ouvrir la WS pour les matchs récents
  socket = new WebSocket(
    `ws://localhost:8080/ws/match-stream?script=producteurRecentMatch.py&accountId=${playerId}`
  )
  socket.onopen = () => console.log('✅ WS connectée')
  socket.onerror = e => console.error('❌ WS erreur', e)
  socket.onclose = () => console.log('🔌 WS fermée')

  socket.onmessage = async (e) => {
    let data
    try {
      data = JSON.parse(e.data)
    } catch {
      return
    }

    // 3) Pour chaque match, récupérer le nom du héros
    const withNames = await Promise.all(
      data.map(async m => {
        let heroName = `Héros ${m.hero_id}`
        try {
          const r = await axios.get(
            `/heroes/${m.hero_id}`,
            {
              headers: { Authorization: `Bearer ${token}` },
              withCredentials: true
            }
          )
          heroName = r.data.name
        } catch {
          console.warn(`Échec chargement héros #${m.hero_id}`)
        }
        return { ...m, heroName }
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
  color: white;
}
.title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 1.5rem;
}
.table-wrapper {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  max-height: 600px;
  overflow-y: auto;
  overflow-x: auto;
}
.recent-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 15px;
}
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
.table-wrapper table thead th:first-child {
  border-top-left-radius: 8px;
}
.table-wrapper table thead th:last-child {
  border-top-right-radius: 8px;
}
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
.victory-row td { background-color: #e6ffed; }
.defeat-row td  { background-color: #ffeaea; }
.no-games {
  text-align: center;
  color: #888;
  font-style: italic;
  margin-top: 2rem;
}
</style>
