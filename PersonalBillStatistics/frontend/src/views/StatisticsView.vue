<template>
  <div class="stats-page">
    <div class="page-header">
      <div>
        <p class="page-kicker">Insights</p>
        <h2>统计看板</h2>
      </div>
      <div class="picker-group">
        <select v-model="year" @change="fetchAll">
          <option v-for="y in years" :key="y" :value="y">{{ y }}年</option>
        </select>
        <select v-model="month" @change="fetchAll">
          <option :value="null">全部月份</option>
          <option v-for="m in 12" :key="m" :value="m">{{ m }}月</option>
        </select>
      </div>
    </div>

    <div class="charts-grid">
      <div class="chart-card chart-wide">
        <div class="chart-heading">
          <h3>月度收支对比</h3>
          <span>Income / Expense</span>
        </div>
        <div ref="barChart" class="chart-box"></div>
      </div>
      <div class="chart-card">
        <div class="chart-heading">
          <h3>月度支出趋势</h3>
          <span>Trend</span>
        </div>
        <div ref="lineChart" class="chart-box"></div>
      </div>
      <div class="chart-card">
        <div class="chart-heading">
          <h3>支出分类占比</h3>
          <span>Category</span>
        </div>
        <div ref="pieChart" class="chart-box"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCategoryStats, getMonthlyStats, getMonthlyCompare } from '../api/statistics'

export default {
  name: 'StatisticsView',
  data() {
    const currentYear = new Date().getFullYear()
    const years = []
    for (let y = currentYear; y >= currentYear - 5; y--) years.push(y)
    return {
      year: currentYear,
      month: null,
      years,
      barChart: null,
      lineChart: null,
      pieChart: null
    }
  },
  mounted() {
    window.addEventListener('resize', this.resizeCharts)
    this.fetchAll()
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.resizeCharts)
    this.barChart?.dispose()
    this.lineChart?.dispose()
    this.pieChart?.dispose()
  },
  methods: {
    async fetchAll() {
      await Promise.all([this.fetchCompare(), this.fetchTrend(), this.fetchCategory()])
    },
    async fetchCompare() {
      const res = await getMonthlyCompare({ year: this.year })
      if (res.data.code !== 200) return
      const data = res.data.data
      if (!this.barChart) {
        this.barChart = this.$echarts.init(this.$refs.barChart)
      }
      this.barChart.setOption({
        color: ['#16855f', '#d94b54'],
        tooltip: {
          trigger: 'axis',
          backgroundColor: '#172033',
          borderWidth: 0,
          textStyle: { color: '#ffffff' }
        },
        legend: {
          data: ['收入', '支出'],
          bottom: 0,
          textStyle: { color: '#6b7280' },
          padding: [5, 0, 0, 0]
        },
        grid: { left: 50, right: 20, top: 18, bottom: 48 },
        xAxis: {
          type: 'category',
          data: data.map(d => d.month + '月'),
          axisLine: { lineStyle: { color: '#dce5ef' } },
          axisLabel: { color: '#6b7280' },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          splitLine: { lineStyle: { color: '#e8eef5' } },
          axisLabel: { color: '#6b7280' }
        },
        series: [
          { name: '收入', type: 'bar', barMaxWidth: 24, data: data.map(d => d.income), itemStyle: { borderRadius: [6, 6, 0, 0] } },
          { name: '支出', type: 'bar', barMaxWidth: 24, data: data.map(d => d.expense), itemStyle: { borderRadius: [6, 6, 0, 0] } }
        ]
      })
    },
    async fetchTrend() {
      const res = await getMonthlyStats({ year: this.year, type: 'expense' })
      if (res.data.code !== 200) return
      const data = res.data.data
      if (!this.lineChart) {
        this.lineChart = this.$echarts.init(this.$refs.lineChart)
      }
      this.lineChart.setOption({
        tooltip: {
          trigger: 'axis',
          backgroundColor: '#172033',
          borderWidth: 0,
          textStyle: { color: '#ffffff' }
        },
        grid: { left: 50, right: 20, top: 18, bottom: 32 },
        xAxis: {
          type: 'category',
          data: data.map(d => d.month + '月'),
          axisLine: { lineStyle: { color: '#dce5ef' } },
          axisLabel: { color: '#6b7280' },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          splitLine: { lineStyle: { color: '#e8eef5' } },
          axisLabel: { color: '#6b7280' }
        },
        series: [{
          name: '支出', type: 'line', data: data.map(d => d.amount),
          smooth: true, symbolSize: 7,
          itemStyle: { color: '#246bfe' },
          lineStyle: { width: 3, color: '#246bfe' },
          areaStyle: { color: 'rgba(36, 107, 254, 0.12)' }
        }]
      })
    },
    async fetchCategory() {
      const params = { type: 'expense' }
      if (this.month) {
        const mm = String(this.month).padStart(2, '0')
        const lastDay = new Date(this.year, this.month, 0).getDate()
        params.start = `${this.year}-${mm}-01`
        params.end = `${this.year}-${mm}-${lastDay}`
      } else {
        params.start = `${this.year}-01-01`
        params.end = `${this.year}-12-31`
      }
      const res = await getCategoryStats(params)
      if (res.data.code !== 200) return
      const data = res.data.data.filter(d => d.value > 0)
      if (!this.pieChart) {
        this.pieChart = this.$echarts.init(this.$refs.pieChart)
      }
      this.pieChart.setOption({
        color: ['#246bfe', '#009a9a', '#d94b54', '#16855f', '#b56a14', '#7c68d8'],
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)',
          backgroundColor: '#172033',
          borderWidth: 0,
          textStyle: { color: '#ffffff' }
        },
        legend: { bottom: 0, textStyle: { color: '#6b7280' } },
        series: [{
          type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
          data: data.map(d => ({ name: d.name, value: d.value })),
          label: { formatter: '{b}\n{d}%', color: '#172033' },
          itemStyle: { borderColor: '#ffffff', borderWidth: 3 }
        }]
      })
    },
    resizeCharts() {
      this.barChart?.resize()
      this.lineChart?.resize()
      this.pieChart?.resize()
    }
  }
}
</script>

<style scoped>
.stats-page {
  display: grid;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
}

.page-kicker {
  margin: 0 0 4px;
  color: var(--accent);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0;
  text-transform: uppercase;
}

.page-header h2 {
  margin: 0;
  color: var(--ink);
  font-size: clamp(28px, 4vw, 42px);
  line-height: 1.1;
  letter-spacing: 0;
}

.picker-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-end;
}

.picker-group select {
  min-height: 42px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: var(--surface);
  color: var(--ink);
  font-size: 14px;
  font-weight: 700;
  outline: none;
  padding: 8px 12px;
}

.picker-group select:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 4px rgba(36, 107, 254, 0.1);
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.chart-card {
  min-width: 0;
  border: 1px solid var(--line);
  border-radius: 22px;
  background: var(--surface);
  box-shadow: var(--shadow);
  padding: 20px;
}

.chart-heading {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.chart-heading h3 {
  margin: 0;
  color: var(--ink);
  font-size: 16px;
}

.chart-heading span {
  color: var(--muted);
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
}

.chart-box {
  width: 100%;
  height: 330px;
}

.chart-wide {
  grid-column: 1 / -1;
}

@media (max-width: 860px) {
  .page-header {
    align-items: stretch;
    flex-direction: column;
  }

  .picker-group {
    justify-content: stretch;
  }

  .picker-group select {
    flex: 1 1 160px;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-box {
    height: 300px;
  }
}
</style>
