import{I as s,L as o,bn as a,bt as i,B as u,Q as c,f as d}from"./index-AfbY0myR.js";const n={prefix:Math.floor(Math.random()*1e4),current:0},I=Symbol("elIdInjection"),p=()=>u()?c(I,n):n,f=r=>{const e=p();!s&&e===n&&o("IdInjection",`Looks like you are using server rendering, you must provide a id provider to ensure the hydration process to be succeed
usage: app.provide(ID_INJECTION_KEY, {
  prefix: number,
  current: number,
})`);const t=a();return i(()=>d(r)||`${t.value}-id-${e.prefix}-${e.current++}`)};export{p as a,f as u};
