<template>
  <div class="auth-container">
    <section class="auth-intro">
      <p class="eyebrow">pocket-budget</p>
      <h1>你的个人收支数据中心。</h1>
      <p>用更清晰的记录、筛选和图表，把每天的资金流动变成可追踪的数据资产。</p>
      <div class="auth-preview">
        <span></span>
        <strong>记录 · 分类 · 统计</strong>
        <small>登录后查看你的个人预算数据</small>
      </div>
    </section>
    <div class="auth-card">
      <h2>登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" type="text" required placeholder="请输入用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" required placeholder="请输入密码" />
        </div>
        <p class="error-msg" v-if="error">{{ error }}</p>
        <button type="submit" class="btn-primary">登录</button>
      </form>
      <p class="switch-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import { login } from '../api/user'

export default {
  name: 'LoginView',
  data() {
    return {
      form: { username: '', password: '' },
      error: ''
    }
  },
  methods: {
    async handleLogin() {
      this.error = ''
      try {
        const res = await login(this.form)
        if (res.data.code === 200) {
          this.$emit('login', res.data.data.username)
        } else {
          this.error = res.data.message
        }
      } catch (e) {
        if (e.response) {
          this.error = e.response.data?.message || '服务器错误(' + e.response.status + ')'
        } else if (e.request) {
          this.error = '无法连接到服务器，请确认后端已启动(端口8080)'
        } else {
          this.error = e.message
        }
      }
    }
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100dvh;
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(320px, 420px);
  align-items: center;
  gap: clamp(28px, 6vw, 72px);
  padding: clamp(28px, 7vw, 84px) 0;
}

.auth-intro {
  max-width: 610px;
}

.eyebrow {
  margin: 0 0 14px;
  color: var(--accent);
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0;
  text-transform: uppercase;
}

.auth-intro h1 {
  margin: 0;
  color: var(--ink);
  font-size: clamp(38px, 6vw, 68px);
  line-height: 1.02;
  letter-spacing: 0;
}

.auth-intro p:not(.eyebrow) {
  max-width: 520px;
  margin: 22px 0 0;
  color: var(--muted);
  font-size: 17px;
}

.auth-preview {
  width: min(100%, 380px);
  margin-top: 34px;
  padding: 22px;
  border: 1px solid var(--line);
  border-radius: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: var(--shadow);
}

.auth-preview span {
  display: block;
  width: 58px;
  height: 8px;
  border-radius: 99px;
  background: var(--cyan);
}

.auth-preview strong {
  display: block;
  margin-top: 28px;
  color: var(--ink);
  font-size: 40px;
  line-height: 1;
}

.auth-preview small {
  display: block;
  margin-top: 8px;
  color: var(--muted);
  font-weight: 700;
}

.auth-card {
  width: 100%;
  padding: 34px;
  border: 1px solid var(--line);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: var(--shadow);
}

.auth-card h2 {
  margin: 0 0 26px;
  color: var(--ink);
  font-size: 24px;
  letter-spacing: 0;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--ink);
  font-size: 14px;
  font-weight: 700;
}

.form-group input {
  width: 100%;
  min-height: 46px;
  padding: 10px 14px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: var(--surface-soft);
  color: var(--ink);
  font-size: 15px;
  outline: none;
}

.form-group input:focus {
  border-color: var(--accent);
  background: #fff;
  box-shadow: 0 0 0 4px rgba(36, 107, 254, 0.1);
}

.error-msg {
  margin: 0 0 14px;
  border-left: 3px solid var(--danger);
  background: var(--danger-soft);
  color: var(--danger);
  font-size: 13px;
  font-weight: 700;
  padding: 9px 10px;
}

.btn-primary {
  width: 100%;
  min-height: 46px;
  border: 1px solid var(--accent-dark);
  border-radius: 14px;
  background: var(--accent);
  color: #fff;
  cursor: pointer;
  font-size: 15px;
  font-weight: 800;
}

.btn-primary:hover {
  background: var(--accent-dark);
  transform: translateY(-1px);
}

.switch-link {
  margin: 20px 0 0;
  color: var(--muted);
  font-size: 14px;
  text-align: center;
}

.switch-link a {
  color: var(--accent);
  font-weight: 800;
}

@media (max-width: 820px) {
  .auth-container {
    grid-template-columns: 1fr;
    padding-top: 32px;
  }

  .auth-intro h1 {
    font-size: 38px;
  }
}
</style>
