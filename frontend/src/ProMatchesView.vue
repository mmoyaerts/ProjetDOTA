<template>
  <div class="container">
    <!-- Titre dynamique -->
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

const route      = useRoute()
const playerId   = route.params.id
const token = localStorage.getItem('jwt_token')

// État
const proName       = ref('…')
const recentMatches = ref([])

let socket = null

onMounted(async () => {
  // 1) Charger le nom du pro
  try {
      const res = await axios.get(
            `http://localhost:8080/heroes/${m.hero_id}`,
            {
              headers: { Authorization: `Bearer ${token}` },
              withCredentials: true
            }
          )
    proName.value = res.data.pseudo || res.data.name || `#${playerId}`
  } catch (err) {
    console.error('Erreur récupération du pro :', err)
    proName.value = `#${playerId}`
  }

  // 2) Ouvrir le WebSocket pour les matchs récents
  socket = new WebSocket(
    `ws://localhost:8080/ws/match-stream?script=producteurRecentMatch.py&accountId=${playerId}`
  )
  socket.onopen = () => console.log('✅ WS match-stream connecté')
  socket.onerror = err => console.error('❌ WS erreur :', err)
  socket.onclose = () => console.log('🔌 WS fermé')

  socket.onmessage = async (e) => {
    let data
    try {
      data = JSON.parse(e.data)
    } catch {
      console.warn('Donnée WS non JSON:', e.data)
      return
    }

    // Récupère le nom du héros pour chaque match
    const withNames = await Promise.all(
      data.map(async m => {
        let name = `Héros ${m.hero_id}`
        try {
          const res = await axios.get(
            `http://localhost:8080/heroes/${m.hero_id}`,
            {
              headers: { Authorization: `Bearer ${token}` },
              withCredentials: true
            }
          )
          name = res.data.name
        } catch (e) {
          console.error(`Erreur héros ${m.hero_id}:`, e)
        }
        return { ...m, heroName: name }
      })
    )

    recentMatches.value = withNames
  }
})

onBeforeUnmount(() => {
  if (socket) socket.close()
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

/* Titre */
.title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 1.5rem;
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
