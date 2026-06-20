<template>
  <div id="app" :class="{ 'has-shell': isLoggedIn }">
    <header class="app-header" v-if="isLoggedIn">
      <router-link to="/bills" class="brand-mark">
        <span class="brand-symbol">SD</span>
        <span>
          <strong>pocket-budget</strong>
          <small>Budget Tracker</small>
        </span>
      </router-link>
      <nav class="nav-bar">
        <router-link to="/bills" class="nav-link">收支记录</router-link>
        <router-link to="/statistics" class="nav-link">统计看板</router-link>
      </nav>
      <div class="session-box">
        <span class="user-info">{{ username }}</span>
        <button class="btn-logout" @click="handleLogout">退出登录</button>
      </div>
    </header>
    <main class="app-main">
      <router-view @login="onLogin" />
    </main>
  </div>
</template>

<script>
import { logout } from './api/user'

export default {
  name: 'App',
  data() {
    return {
      username: sessionStorage.getItem('username') || ''
    }
  },
  computed: {
    isLoggedIn() {
      return !!this.username
    }
  },
  methods: {
    onLogin(username) {
      this.username = username
      sessionStorage.setItem('username', username)
      this.$router.push('/bills')
    },
    async handleLogout() {
      await logout()
      sessionStorage.removeItem('username')
      this.username = ''
      this.$router.push('/login')
    }
  }
}
</script>

<style>
:root {
  --ink: #172033;
  --muted: #6b7280;
  --canvas: #eef3f8;
  --surface: #ffffff;
  --surface-soft: #f8fbff;
  --line: #dce5ef;
  --line-strong: #b8c7d8;
  --accent: #246bfe;
  --accent-dark: #174fd1;
  --accent-soft: #e8f0ff;
  --cyan: #009a9a;
  --cyan-soft: #def7f4;
  --warning: #b56a14;
  --warning-soft: #fff3da;
  --danger: #d94b54;
  --danger-soft: #ffe8ea;
  --success: #16855f;
  --success-soft: #e4f8ef;
  --shadow: 0 16px 42px rgba(23, 32, 51, 0.1);
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  min-width: 320px;
  background: var(--canvas);
  color: var(--ink);
  font-family: Inter, "Segoe UI", "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
  font-size: 16px;
  line-height: 1.5;
  font-synthesis: none;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

button,
input,
select {
  font: inherit;
}

button,
a,
select,
input {
  transition: border-color 0.18s ease, background-color 0.18s ease, color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;
}

button {
  min-height: 40px;
}

button:focus-visible,
a:focus-visible,
input:focus-visible,
select:focus-visible {
  outline: 3px solid rgba(36, 107, 254, 0.2);
  outline-offset: 2px;
}

#app {
  min-height: 100dvh;
}

.app-header {
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 20;
  width: 252px;
  display: flex;
  flex-direction: column;
  gap: 22px;
  padding: 22px 18px;
  border-right: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 12px 0 32px rgba(23, 32, 51, 0.06);
  backdrop-filter: blur(18px);
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-height: 46px;
  color: var(--ink);
  text-decoration: none;
}

.brand-symbol {
  display: grid;
  width: 44px;
  height: 44px;
  place-items: center;
  border-radius: 14px;
  background: var(--accent);
  color: #fff;
  box-shadow: 0 12px 28px rgba(36, 107, 254, 0.28);
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0;
}

.brand-mark strong,
.brand-mark small {
  display: block;
  letter-spacing: 0;
}

.brand-mark strong {
  font-size: 15px;
}

.brand-mark small {
  margin-top: 1px;
  color: var(--muted);
  font-size: 12px;
}

.nav-bar {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-link {
  min-height: 46px;
  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 14px;
  border: 1px solid transparent;
  border-radius: 14px;
  color: var(--muted);
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
}

.nav-link:hover,
.nav-link.router-link-active {
  border-color: rgba(36, 107, 254, 0.2);
  background: var(--accent-soft);
  color: var(--accent-dark);
  box-shadow: inset 3px 0 0 var(--accent);
}

.session-box {
  margin-top: auto;
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
}

.user-info {
  max-width: 140px;
  overflow: hidden;
  color: var(--muted);
  font-size: 14px;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-logout {
  border: 1px solid var(--line);
  border-radius: 12px;
  background: var(--surface);
  color: var(--muted);
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
  padding: 0 16px;
}

.btn-logout:hover {
  border-color: var(--danger);
  background: var(--danger-soft);
  color: var(--danger);
}

.app-main {
  width: min(1180px, calc(100% - 32px));
  margin: 0 auto;
  padding: 28px 0 48px;
}

.has-shell .app-main {
  margin-left: calc(252px + max(24px, (100vw - 252px - 1180px) / 2));
  margin-right: auto;
}

@media (max-width: 760px) {
  .app-header {
    position: sticky;
    inset: auto;
    top: 0;
    width: 100%;
    flex-direction: column;
    gap: 12px;
    padding: 12px 16px;
    border-right: none;
    border-bottom: 1px solid var(--line);
  }

  .nav-bar {
    flex-direction: row;
    justify-content: flex-start;
    overflow-x: auto;
    padding-bottom: 2px;
  }

  .session-box {
    justify-content: space-between;
    padding-top: 10px;
  }

  .app-main {
    width: min(100% - 24px, 1180px);
    padding-top: 18px;
  }

  .has-shell .app-main {
    margin-left: auto;
  }
}
</style>
