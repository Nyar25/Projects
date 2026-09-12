import React, { useState, useMemo, useEffect } from "react";

/* ============================================================
   AGRISTOCK V3 — Simplifié : moins de clics, gros boutons,
   admin facile, chauffeur ultra-rapide.
   ============================================================ */

// ---------- Utilitaires ----------
const uid = () => Math.random().toString(36).slice(2, 10);
const fmt = (n, d = 0) =>
  (Number.isFinite(n) ? n : 0).toLocaleString("fr-FR", { minimumFractionDigits: d, maximumFractionDigits: d });
const todayISO = () => new Date().toISOString().slice(0, 10);
const yearOf = (iso) => (iso || todayISO()).slice(0, 4);
const monthOf = (iso) => (iso || todayISO()).slice(0, 7);
const MOIS_FR = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"];
function monthLabel(mk) {
  const [y, m] = mk.split("-");
  return `${MOIS_FR[parseInt(m, 10) - 1]} ${y}`;
}
function listYears(mouvements) {
  const ys = new Set(mouvements.map((m) => yearOf(m.date)));
  ys.add(yearOf(todayISO()));
  return Array.from(ys).sort().reverse();
}
function groupByMonth(mouvements, year) {
  const sorted = [...mouvements].sort((a, b) => (a.date < b.date ? -1 : 1));
  let running = 0;
  const byMonth = {};
  sorted.forEach((m) => {
    const signed = m.type === "sortie" ? -m.quantite : m.quantite;
    running += signed;
    if (yearOf(m.date) !== year) return;
    const mk = monthOf(m.date);
    if (!byMonth[mk]) byMonth[mk] = { entrees: 0, sorties: 0, mouvements: [], soldeFin: running };
    if (signed > 0) byMonth[mk].entrees += m.quantite;
    else byMonth[mk].sorties += m.quantite;
    byMonth[mk].mouvements.push(m);
    byMonth[mk].soldeFin = running;
  });
  return byMonth;
}

// ---------- Logo Agristock ----------
function AgristockMark({ size = 40, tone = "field" }) {
  const tones = {
    field: { a: "#4A7C3F", a2: "#5E9950", b: "#F5F0E6" },
    admin: { a: "#C97B3D", a2: "#DC9455", b: "#F5F0E6" },
    driver: { a: "#1C2B1E", a2: "#2E4530", b: "#F5F0E6" },
  };
  const { a, a2, b } = tones[tone] || tones.field;
  const gid = `mark-grad-${tone}`;
  return (
    <svg width={size} height={size} viewBox="0 0 64 64" aria-hidden="true" style={{ filter: "drop-shadow(0 6px 14px rgba(0,0,0,0.18))" }}>
      <defs>
        <linearGradient id={gid} x1="0" y1="0" x2="64" y2="64" gradientUnits="userSpaceOnUse">
          <stop offset="0" stopColor={a2} />
          <stop offset="1" stopColor={a} />
        </linearGradient>
      </defs>
      <rect width="64" height="64" rx="16" fill={`url(#${gid})`} />
      <rect x="14" y="26" width="16" height="24" rx="2" fill={b} />
      <path d="M14 26 Q22 16 30 26 Z" fill={b} />
      <rect x="19" y="32" width="6" height="18" fill={a} opacity="0.35" />
      <g transform="translate(36,12)">
        <rect x="6.5" y="6" width="3" height="30" fill={b} />
        {[0, 1, 2, 3, 4].map((i) => (
          <g key={i}>
            <ellipse cx={4} cy={10 + i * 6} rx="4.5" ry="2.6" fill={b} transform={`rotate(-25 4 ${10 + i * 6})`} />
            <ellipse cx={12} cy={10 + i * 6} rx="4.5" ry="2.6" fill={b} transform={`rotate(25 12 ${10 + i * 6})`} />
          </g>
        ))}
      </g>
    </svg>
  );
}

function Wordmark({ size = 28, color = "currentColor" }) {
  return (
    <div className="font-[800] tracking-tight" style={{ fontFamily: "Archivo, sans-serif", fontSize: size, color }}>
      AGRISTOCK
    </div>
  );
}

// ---------- Données de démo ----------
// Exploitation type : ~200 ha de céréales (moisson) + ~200 ha d'herbe
const initialParcelles = [
  // Céréales — total 200 ha
  { id: uid(), nom: "La Grande Pièce", surface: 42, type: "cereale", culture: "Blé tendre" },
  { id: uid(), nom: "Les Brunes", surface: 35, type: "cereale", culture: "Blé tendre" },
  { id: uid(), nom: "Le Clos Rond", surface: 28, type: "cereale", culture: "Orge" },
  { id: uid(), nom: "La Plaine", surface: 38, type: "cereale", culture: "Orge" },
  { id: uid(), nom: "Les Terres Rouges", surface: 30, type: "cereale", culture: "Colza" },
  { id: uid(), nom: "Le Champtier", surface: 27, type: "cereale", culture: "Blé tendre" },
  // Herbe / prairies — total 200 ha
  { id: uid(), nom: "Champ du Bois", surface: 45, type: "herbe", culture: "Prairie" },
  { id: uid(), nom: "La Prairie Basse", surface: 50, type: "herbe", culture: "Prairie" },
  { id: uid(), nom: "Le Pâtis", surface: 38, type: "herbe", culture: "Prairie" },
  { id: uid(), nom: "Les Communaux", surface: 35, type: "herbe", culture: "Prairie" },
  { id: uid(), nom: "La Côte", surface: 32, type: "herbe", culture: "Prairie" },
];
const COURS_DEFAUT = [
  { id: uid(), categorie: "Céréale", nom: "Blé tendre (rendu Rouen)", valeur: 190.75, unite: "€/t", maj: todayISO() },
  { id: uid(), categorie: "Céréale", nom: "Orge fourragère (rendu Rouen)", valeur: 196.0, unite: "€/t", maj: todayISO() },
  { id: uid(), categorie: "Céréale", nom: "Maïs (rendu Bordeaux)", valeur: 211.5, unite: "€/t", maj: todayISO() },
  { id: uid(), categorie: "Céréale", nom: "Colza (FOB Moselle)", valeur: 519.0, unite: "€/t", maj: todayISO() },
  { id: uid(), categorie: "Engrais", nom: "Ammonitrate 33,5%", valeur: 320, unite: "€/t", maj: todayISO() },
  { id: uid(), categorie: "Engrais", nom: "Urée 46%", valeur: 430, unite: "€/t", maj: todayISO() },
  { id: uid(), categorie: "Carburant", nom: "GNR", valeur: 0.75, unite: "€/L", maj: todayISO() },
];

// ============================================================
// UI DE BASE — gros boutons, peu de texte
// ============================================================
function BigButton({ icon, label, sub, tone = "field", onClick, className = "" }) {
  const ring = {
    field: "hover:border-[#4A7C3F]/50 hover:shadow-[#4A7C3F]/10",
    admin: "hover:border-[#C97B3D]/50 hover:shadow-[#C97B3D]/10",
    driver: "hover:border-[#1C2B1E]/50 hover:shadow-[#1C2B1E]/10",
  };
  return (
    <button
      onClick={onClick}
      className={`group flex flex-col items-center justify-center gap-3 bg-white border-2 border-[#1C2B1E]/10 ${ring[tone]} rounded-3xl py-8 px-4 shadow-sm hover:shadow-lg hover:-translate-y-0.5 active:translate-y-0 active:scale-[0.97] transition-all duration-200 ${className}`}
    >
      <span className="transition-transform duration-200 group-hover:scale-110">{icon}</span>
      <span className="font-bold text-lg text-center leading-tight">{label}</span>
      {sub && <span className="text-xs text-[#1C2B1E]/50 text-center">{sub}</span>}
    </button>
  );
}

function ActionButton({ children, tone = "primary", size = "lg", className = "", ...props }) {
  const tones = {
    primary: "bg-[#4A7C3F] text-white shadow-[#4A7C3F]/25 active:bg-[#345a2c]",
    admin: "bg-[#C97B3D] text-white shadow-[#C97B3D]/25 active:bg-[#9c5c29]",
    driver: "bg-[#1C2B1E] text-white shadow-[#1C2B1E]/25 active:bg-[#0e1610]",
    ghost: "bg-[#F5F0E6] text-[#1C2B1E] shadow-none active:bg-[#ece4d2]",
    danger: "bg-[#D6483A] text-white shadow-[#D6483A]/25 active:bg-[#a8362c]",
  };
  const sizes = { lg: "py-5 text-lg", md: "py-3.5 text-base", sm: "py-2.5 text-sm" };
  return (
    <button
      className={`w-full rounded-2xl font-bold shadow-md transition-all duration-150 hover:brightness-105 active:scale-[0.98] disabled:opacity-40 disabled:shadow-none disabled:hover:brightness-100 ${tones[tone]} ${sizes[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}

function BigInput({ className = "", ...props }) {
  return (
    <input
      className={`w-full px-5 py-4 rounded-2xl border-2 border-[#1C2B1E]/15 bg-white shadow-sm text-xl font-semibold text-center transition-all duration-150 focus:outline-none focus:border-[#4A7C3F] focus:shadow-[0_0_0_4px_rgba(74,124,63,0.12)] ${className}`}
      {...props}
    />
  );
}

function Card({ children, className = "" }) {
  return <div className={`bg-white rounded-3xl border border-[#1C2B1E]/8 shadow-sm ${className}`}>{children}</div>;
}

function BigStat({ label, value, tone = "field" }) {
  const colors = { field: "#4A7C3F", admin: "#C97B3D", driver: "#1C2B1E", alert: "#D6483A" };
  const color = colors[tone];
  return (
    <div className="relative rounded-2xl pl-5 pr-4 py-4 bg-[#F5F0E6] flex-1 min-w-[130px] text-center overflow-hidden">
      <span className="absolute left-0 top-1/2 -translate-y-1/2 h-[60%] w-[3px] rounded-full" style={{ background: color }} />
      <div className="text-[0.72rem] uppercase tracking-wide font-bold opacity-55">{label}</div>
      <div className="text-3xl font-extrabold tabular-nums mt-1" style={{ color }}>
        {value}
      </div>
    </div>
  );
}

// Choix en un clic, gros, type "puces" — remplace les <select> et menus à étapes
function PillChoice({ options, value, onChange, tone = "admin", columns = 2 }) {
  const active = {
    admin: "bg-[#C97B3D] text-white shadow-md shadow-[#C97B3D]/25",
    driver: "bg-[#1C2B1E] text-white shadow-md shadow-[#1C2B1E]/25",
    field: "bg-[#4A7C3F] text-white shadow-md shadow-[#4A7C3F]/25",
  };
  return (
    <div className={`grid gap-2`} style={{ gridTemplateColumns: `repeat(${columns}, minmax(0,1fr))` }}>
      {options.map((o) => (
        <button
          key={o.value}
          type="button"
          onClick={() => onChange(o.value)}
          className={`py-3 px-3 rounded-xl text-sm font-bold transition-all duration-150 active:scale-[0.97] ${value === o.value ? active[tone] : "bg-[#F5F0E6] text-[#1C2B1E]/65 hover:bg-[#ece4d2]"}`}
        >
          {o.label}
        </button>
      ))}
    </div>
  );
}

function ScreenHeader({ title, onBack, tone = "field" }) {
  const bg = {
    field: "bg-gradient-to-b from-[#57905F] to-[#4A7C3F]",
    admin: "bg-gradient-to-b from-[#D28E51] to-[#C97B3D]",
    driver: "bg-gradient-to-b from-[#2A3D2C] to-[#1C2B1E]",
  };
  return (
    <div className={`${bg[tone]} text-white px-5 pt-6 pb-5 flex items-center gap-3 sticky top-0 z-10 shadow-md`}>
      {onBack && (
        <button onClick={onBack} className="w-10 h-10 rounded-full bg-white/15 active:bg-white/25 hover:bg-white/20 transition-colors flex items-center justify-center flex-shrink-0">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <path d="M19 12H5M12 19l-7-7 7-7" />
          </svg>
        </button>
      )}
      <h1 className="font-extrabold text-xl flex-1">{title}</h1>
    </div>
  );
}

function openPdfWindow(title, bodyHtml) {
  const w = window.open("", "_blank");
  if (!w) return;
  w.document.write(`
    <html><head><title>${title}</title><meta charset="utf-8" />
    <style>
      body{font-family:Arial,sans-serif;padding:32px;color:#1C2B1E}
      h1{font-size:20px;border-bottom:3px solid #4A7C3F;padding-bottom:8px}
      table{width:100%;border-collapse:collapse;margin-top:16px}
      th,td{border:1px solid #ccc;padding:8px 10px;text-align:left;font-size:13px}
      th{background:#F5F0E6} .total-row td{font-weight:bold;background:#F5F0E6}
      .meta{color:#555;font-size:12px;margin-bottom:4px}
    </style></head><body>
    <h1>${title}</h1>${bodyHtml}
    <script>window.onload=()=>setTimeout(()=>window.print(),200)</script>
    </body></html>`);
  w.document.close();
}

// ---------- Stockage persistant des comptes (survit à la fermeture du navigateur) ----------
// Utilise localStorage quand disponible (vrai navigateur / site web), et ne casse jamais
// si l'environnement l'interdit (ex: certains aperçus sandboxés) — dans ce cas, le compte
// ne sera mémorisé que pendant la session en cours.
const ACCOUNTS_STORAGE_KEY = "agristock_accounts_v1";
function loadStoredAccounts() {
  try {
    const raw = window.localStorage.getItem(ACCOUNTS_STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch (e) {
    return [];
  }
}
function saveStoredAccounts(accounts) {
  try {
    window.localStorage.setItem(ACCOUNTS_STORAGE_KEY, JSON.stringify(accounts));
  } catch (e) {
    // Stockage indisponible (navigation privée, environnement restreint, etc.) : on ignore silencieusement.
  }
}

/* ============================================================
   SPLASH
   ============================================================ */
function SplashScreen({ onContinue }) {
  return (
    <div
      className="screen-in min-h-screen flex flex-col items-center justify-center text-white px-6"
      style={{ background: "radial-gradient(circle at 50% 30%, #2A3D2C 0%, #1C2B1E 70%)" }}
    >
      <div style={{ animation: "screenIn 0.5s ease-out both" }}>
        <AgristockMark size={100} tone="field" />
      </div>
      <div className="mt-6 text-center">
        <Wordmark size={40} color="#fff" />
        <div className="text-sm uppercase tracking-[0.2em] opacity-55 mt-2">Gestion de ferme</div>
      </div>
      <ActionButton tone="primary" className="mt-12 max-w-xs" onClick={onContinue}>
        Démarrer
      </ActionButton>
    </div>
  );
}

/* ============================================================
   CONNEXION — email + mot de passe (admin)
   ============================================================ */
const ORIGINES_INSCRIPTION = [
  "Bouche à oreille",
  "Réseaux sociaux",
  "Recherche internet",
  "Salon ou événement agricole",
  "Coopérative agricole",
  "Presse / magazine agricole",
  "Publicité",
  "Autre",
];

function LoginScreen({ accounts, onLogin, onCreateAccount }) {
  const [mode, setMode] = useState("login");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [newEmail, setNewEmail] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [farmName, setFarmName] = useState("");
  const [newAdminCode, setNewAdminCode] = useState("");
  const [origine, setOrigine] = useState("");
  const [pontBascule, setPontBascule] = useState(""); // "oui" | "non"
  const [verifie, setVerifie] = useState(false);

  function handleLogin() {
    const acc = accounts.find((a) => a.email === email.trim().toLowerCase() && a.password === password);
    if (!acc) {
      setError("Email ou mot de passe incorrect.");
      return;
    }
    setError("");
    onLogin(acc);
  }

  // Contrôles de validité par champ (utilisés par le bouton de vérification et par l'affichage des ✓)
  const emailValide = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(newEmail.trim());
  const passwordValide = newPassword.length >= 4;
  const codeValide = newAdminCode.length === 4;
  const farmValide = farmName.trim().length > 0;
  const origineValide = origine !== "";
  const pontBasculeValide = pontBascule !== "";
  const emailDejaPris = accounts.some((a) => a.email === newEmail.trim().toLowerCase());

  function verifierInformations() {
    if (!farmValide) { setError("Indiquez le nom de l'exploitation."); setVerifie(false); return; }
    if (!emailValide) { setError("L'adresse email n'est pas valide."); setVerifie(false); return; }
    if (emailDejaPris) { setError("Un compte existe déjà avec cet email."); setVerifie(false); return; }
    if (!passwordValide) { setError("Le mot de passe doit faire au moins 4 caractères."); setVerifie(false); return; }
    if (!codeValide) { setError("Le code administrateur doit faire 4 chiffres."); setVerifie(false); return; }
    if (!origineValide) { setError("Indiquez comment vous avez connu Agristock."); setVerifie(false); return; }
    if (!pontBasculeValide) { setError("Indiquez si vous disposez d'un pont bascule."); setVerifie(false); return; }
    setError("");
    setVerifie(true);
  }

  function handleCreate() {
    if (!verifie) {
      verifierInformations();
      return;
    }
    setError("");
    const acc = { email: newEmail.trim().toLowerCase(), password: newPassword, farmName: farmName.trim(), adminCode: newAdminCode, origine, pontBascule: pontBascule === "oui" };
    onCreateAccount(acc);
    onLogin(acc);
  }

  return (
    <div className="screen-in min-h-screen bg-white flex flex-col">
      <div className="px-6 pt-14 pb-6 flex flex-col items-center">
        <AgristockMark size={64} tone="field" />
        <div className="mt-3"><Wordmark size={26} color="#1C2B1E" /></div>
      </div>

      <div className="flex-1 px-6 pb-10 flex flex-col justify-center">
        <div className="max-w-sm mx-auto space-y-4">
          {mode === "login" ? (
            <>
              <h1 className="text-xl font-extrabold text-center mb-5">Connexion</h1>
              <BigInput
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Email"
                className="text-base text-left"
              />
              <BigInput
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Mot de passe"
                className="text-base text-left"
              />
              {error && <p className="text-sm text-[#D6483A] font-bold text-center">{error}</p>}
              <ActionButton tone="primary" onClick={handleLogin} disabled={!email.trim() || !password}>Continuer</ActionButton>
              <button className="block w-full text-center text-sm font-bold text-[#4A7C3F] py-2" onClick={() => { setMode("create"); setError(""); }}>
                Créer un compte
              </button>
            </>
          ) : (
            <>
              <h1 className="text-xl font-extrabold text-center mb-5">Créer un compte</h1>

              <div>
                <BigInput value={farmName} onChange={(e) => { setFarmName(e.target.value); setVerifie(false); }} placeholder="Nom de l'exploitation" className="text-base text-left" />
                {farmName.trim() && <p className="text-xs font-bold text-[#4A7C3F] mt-1 px-1">{farmValide ? "✓ Nom renseigné" : ""}</p>}
              </div>

              <div>
                <BigInput type="email" value={newEmail} onChange={(e) => { setNewEmail(e.target.value); setVerifie(false); }} placeholder="Email" className="text-base text-left" />
                {newEmail.trim() && (
                  <p className={`text-xs font-bold mt-1 px-1 ${emailValide && !emailDejaPris ? "text-[#4A7C3F]" : "text-[#D6483A]"}`}>
                    {emailDejaPris ? "✕ Un compte existe déjà avec cet email" : emailValide ? "✓ Email valide" : "✕ Format d'email invalide"}
                  </p>
                )}
              </div>

              <div>
                <BigInput type="password" value={newPassword} onChange={(e) => { setNewPassword(e.target.value); setVerifie(false); }} placeholder="Mot de passe" className="text-base text-left" />
                {newPassword && <p className={`text-xs font-bold mt-1 px-1 ${passwordValide ? "text-[#4A7C3F]" : "text-[#D6483A]"}`}>{passwordValide ? "✓ Mot de passe valide" : "✕ 4 caractères minimum"}</p>}
              </div>

              <div>
                <BigInput
                  type="text"
                  inputMode="numeric"
                  pattern="[0-9]*"
                  value={newAdminCode}
                  onChange={(e) => { setNewAdminCode(e.target.value.replace(/\D/g, "").slice(0, 4)); setVerifie(false); }}
                  placeholder="Code administrateur (4 chiffres)"
                  className="text-base text-left"
                />
                <p className="text-xs text-[#1C2B1E]/45 mt-1.5 px-1">Ce code sera demandé pour accéder à l'espace administrateur (différent du chauffeur).</p>
                {newAdminCode.length > 0 && <p className={`text-xs font-bold mt-1 px-1 ${codeValide ? "text-[#4A7C3F]" : "text-[#D6483A]"}`}>{codeValide ? "✓ Code à 4 chiffres" : `✕ ${4 - newAdminCode.length} chiffre(s) restant(s)`}</p>}
              </div>

              <div>
                <select
                  value={origine}
                  onChange={(e) => { setOrigine(e.target.value); setVerifie(false); }}
                  className="w-full px-5 py-4 rounded-2xl border-2 border-[#1C2B1E]/15 bg-white shadow-sm text-base font-semibold text-left"
                >
                  <option value="">Comment avez-vous connu Agristock ?</option>
                  {ORIGINES_INSCRIPTION.map((o) => <option key={o} value={o}>{o}</option>)}
                </select>
                {origine && <p className="text-xs font-bold text-[#4A7C3F] mt-1 px-1">✓ Merci !</p>}
              </div>

              <div>
                <p className="text-sm font-bold text-[#1C2B1E]/60 mb-2 px-1 text-left">Disposez-vous d'un pont bascule ?</p>
                <PillChoice
                  tone="field"
                  columns={2}
                  value={pontBascule}
                  onChange={(v) => { setPontBascule(v); setVerifie(false); }}
                  options={[{ value: "oui", label: "Oui" }, { value: "non", label: "Non" }]}
                />
              </div>

              {error && <p className="text-sm text-[#D6483A] font-bold text-center">{error}</p>}

              {!verifie ? (
                <ActionButton tone="ghost" onClick={verifierInformations}>
                  Vérifier mes informations
                </ActionButton>
              ) : (
                <p className="text-sm font-extrabold text-[#4A7C3F] text-center">✓ Informations vérifiées</p>
              )}

              <ActionButton
                tone="primary"
                onClick={handleCreate}
                disabled={!verifie}
              >
                Continuer
              </ActionButton>
              <button className="block w-full text-center text-sm font-bold text-[#4A7C3F] py-2" onClick={() => { setMode("login"); setError(""); setVerifie(false); }}>
                ← Retour à la connexion
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

/* ============================================================
   CHOIX DE RÔLE — 2 grosses icônes, code admin en 1 champ simple
   ============================================================ */
function RoleSelectScreen({ onEnterAdmin, onEnterDriver, adminCode }) {
  const [view, setView] = useState("select"); // select | admin-code | driver-pick
  const [code, setCode] = useState("");
  const [error, setError] = useState("");

  function validerCode() {
    const saisi = code.replace(/\D/g, "");
    if (!adminCode) {
      setError("Aucun code administrateur n'est défini sur ce compte. Reconnectez-vous ou recréez le compte.");
      return;
    }
    if (saisi.length === 0) {
      setError("Merci d'entrer votre code à 4 chiffres.");
      return;
    }
    if (saisi === adminCode) {
      setError("");
      onEnterAdmin();
    } else {
      setError("Code incorrect, réessayez.");
      setCode("");
    }
  }

  if (view === "select") {
    return (
      <div className="screen-in min-h-screen bg-white flex flex-col items-center justify-center px-6">
        <h1 className="text-2xl font-extrabold text-center mb-2">Qui êtes-vous ?</h1>
        <p className="text-sm text-[#1C2B1E]/45 text-center mb-10">Choisissez votre espace pour continuer</p>
        <div className="grid grid-cols-2 gap-5 w-full max-w-md">
          <BigButton
            tone="admin"
            icon={<AgristockMark size={68} tone="admin" />}
            label="Administrateur"
            onClick={() => setView("admin-code")}
          />
          <BigButton
            tone="driver"
            icon={<AgristockMark size={68} tone="driver" />}
            label="Chauffeur"
            onClick={() => setView("driver-pick")}
          />
        </div>
      </div>
    );
  }

  if (view === "admin-code") {
    return (
      <div className="screen-in min-h-screen bg-white flex flex-col items-center px-6 pt-14 pb-8">
        <AgristockMark size={56} tone="admin" />
        <h1 className="text-lg font-extrabold text-center mt-4 mb-6">Code administrateur</h1>
        <div className="w-full max-w-xs space-y-4">
          <BigInput
            autoFocus
            type="text"
            inputMode="numeric"
            pattern="[0-9]*"
            value={code}
            onChange={(e) => { setError(""); setCode(e.target.value.replace(/\D/g, "")); }}
            placeholder="Code"
          />
          {error && <p className="text-base text-[#D6483A] font-extrabold text-center">{error}</p>}
          <ActionButton tone="admin" onClick={validerCode}>
            Continuer
          </ActionButton>
          <button className="block w-full text-center text-sm font-bold text-[#1C2B1E]/45 py-2" onClick={() => { setView("select"); setCode(""); setError(""); }}>
            ← Annuler
          </button>
        </div>
      </div>
    );
  }

  if (view === "driver-pick") {
    return (
      <div className="screen-in min-h-screen bg-white flex flex-col items-center px-6 pt-14 pb-8">
        <AgristockMark size={56} tone="driver" />
        <h1 className="text-lg font-extrabold text-center mt-4 mb-6">Votre prénom</h1>
        <DriverNameForm onSubmit={onEnterDriver} onCancel={() => setView("select")} />
      </div>
    );
  }

  return null;
}

function DriverNameForm({ onSubmit, onCancel }) {
  const [name, setName] = useState("");
  return (
    <div className="w-full max-w-xs space-y-4">
      <BigInput autoFocus value={name} onChange={(e) => setName(e.target.value)} placeholder="Prénom" />
      <ActionButton tone="driver" onClick={() => name.trim() && onSubmit(name.trim())} disabled={!name.trim()}>
        Continuer
      </ActionButton>
      <button className="block w-full text-center text-sm font-bold text-[#1C2B1E]/45 py-2" onClick={onCancel}>
        ← Annuler
      </button>
    </div>
  );
}

/* ============================================================
   DASHBOARD ADMIN — grosses tuiles
   ============================================================ */
const ADMIN_MODULES = [
  { id: "parcelles", label: "Parcelles", emoji: "🌾" },
  { id: "ensilage", label: "Ensilage", emoji: "🚜" },
  { id: "epandage", label: "Épandage", emoji: "🚜" },
  { id: "moisson", label: "Moisson", emoji: "🌽" },
  { id: "pressage", label: "Pressage", emoji: "📦" },
  { id: "phyto", label: "Phytosanitaire", emoji: "🧪" },
  { id: "facturation", label: "Facturation", emoji: "🧾" },
  { id: "infos", label: "Infos & cours", emoji: "📊" },
];

function AdminHome({ onOpen, onLogout, onSwitchRole, pontBascule }) {
  const modules = ADMIN_MODULES.filter((m) => pontBascule || (m.id !== "ensilage" && m.id !== "epandage"));
  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6]">
      <div className="bg-gradient-to-b from-[#D28E51] to-[#C97B3D] text-white px-5 pt-6 pb-7 flex items-center justify-between shadow-md">
        <div className="flex items-center gap-3">
          <AgristockMark size={36} tone="admin" />
          <span className="font-extrabold text-lg">Administrateur</span>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => onOpen("parametres")}
            title="Paramètres"
            aria-label="Paramètres"
            className="w-9 h-9 rounded-full bg-white/15 active:bg-white/25 hover:bg-white/20 transition-colors flex items-center justify-center flex-shrink-0"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="3" />
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z" />
            </svg>
          </button>
          <button
            onClick={onSwitchRole}
            title="Changer de rôle"
            aria-label="Changer de rôle"
            className="w-9 h-9 rounded-full bg-white/15 active:bg-white/25 hover:bg-white/20 transition-colors flex items-center justify-center flex-shrink-0"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M17 2l4 4-4 4" /><path d="M3 11V9a4 4 0 0 1 4-4h14" /><path d="M7 22l-4-4 4-4" /><path d="M21 13v2a4 4 0 0 1-4 4H3" />
            </svg>
          </button>
          <button onClick={onLogout} className="text-sm font-bold bg-white/15 px-3 py-2 rounded-xl active:bg-white/25 hover:bg-white/20 transition-colors">
            Quitter
          </button>
        </div>
      </div>
      <div className="p-5 grid grid-cols-2 gap-4">
        {modules.map((m) => (
          <button
            key={m.id}
            onClick={() => onOpen(m.id)}
            className="group bg-white rounded-3xl p-6 flex flex-col items-center gap-3 border border-[#1C2B1E]/8 shadow-sm hover:shadow-lg hover:-translate-y-0.5 active:translate-y-0 active:scale-[0.97] transition-all duration-200"
          >
            <span className="w-14 h-14 rounded-2xl bg-[#C97B3D]/10 flex items-center justify-center text-3xl transition-transform duration-200 group-hover:scale-110">{m.emoji}</span>
            <span className="font-extrabold text-center">{m.label}</span>
          </button>
        ))}
      </div>
    </div>
  );
}

/* ============================================================
   ACCUEIL CHAUFFEUR — gros boutons
   ============================================================ */
const DRIVER_MODULES = [
  { id: "ensilage", label: "Ensilage", emoji: "🚜" },
  { id: "epandage", label: "Épandage", emoji: "🚜" },
  { id: "moisson", label: "Moisson", emoji: "🌽" },
  { id: "pressage", label: "Pressage", emoji: "📦" },
  { id: "phyto", label: "Phytosanitaire", emoji: "🧪" },
  { id: "stocks", label: "Mes stocks", emoji: "📊" },
  { id: "infos", label: "Infos & cours", emoji: "ℹ️" },
];

function DriverHome({ driverName, onOpen, onLogout, onSwitchRole, pontBascule }) {
  const modules = DRIVER_MODULES.filter((m) => pontBascule || (m.id !== "ensilage" && m.id !== "epandage"));
  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6]">
      <div className="bg-gradient-to-b from-[#2A3D2C] to-[#1C2B1E] text-white px-5 pt-6 pb-7 flex items-center justify-between shadow-md">
        <div className="flex items-center gap-3">
          <AgristockMark size={36} tone="driver" />
          <span className="font-extrabold text-lg">{driverName}</span>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={onSwitchRole}
            title="Changer de rôle"
            aria-label="Changer de rôle"
            className="w-9 h-9 rounded-full bg-white/15 active:bg-white/25 hover:bg-white/20 transition-colors flex items-center justify-center flex-shrink-0"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M17 2l4 4-4 4" /><path d="M3 11V9a4 4 0 0 1 4-4h14" /><path d="M7 22l-4-4 4-4" /><path d="M21 13v2a4 4 0 0 1-4 4H3" />
            </svg>
          </button>
          <button onClick={onLogout} className="text-sm font-bold bg-white/15 px-3 py-2 rounded-xl active:bg-white/25 hover:bg-white/20 transition-colors">Quitter</button>
        </div>
      </div>
      <div className="p-5 grid grid-cols-2 gap-4">
        {modules.map((m) => (
          <button key={m.id} onClick={() => onOpen(m.id)} className="group bg-white rounded-3xl p-6 flex flex-col items-center gap-3 border border-[#1C2B1E]/8 shadow-sm hover:shadow-lg hover:-translate-y-0.5 active:translate-y-0 active:scale-[0.97] transition-all duration-200">
            <span className="w-14 h-14 rounded-2xl bg-[#1C2B1E]/8 flex items-center justify-center text-3xl transition-transform duration-200 group-hover:scale-110">{m.emoji}</span>
            <span className="font-extrabold text-center">{m.label}</span>
          </button>
        ))}
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: PARCELLES — formulaire toujours visible (1 étape de moins)
   ============================================================ */
// Charge pdf.js dynamiquement (une seule fois) pour lire le texte d'un PDF dans le navigateur
let _pdfjsLoadingPromise = null;
function loadPdfJs() {
  if (window.pdfjsLib) return Promise.resolve(window.pdfjsLib);
  if (_pdfjsLoadingPromise) return _pdfjsLoadingPromise;
  _pdfjsLoadingPromise = new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.src = "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.min.js";
    script.onload = () => {
      window.pdfjsLib.GlobalWorkerOptions.workerSrc = "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js";
      resolve(window.pdfjsLib);
    };
    script.onerror = () => reject(new Error("Impossible de charger le lecteur PDF."));
    document.head.appendChild(script);
  });
  return _pdfjsLoadingPromise;
}

// Extrait le texte de toutes les pages d'un PDF (fichier File du navigateur)
async function extractPdfText(file) {
  const pdfjsLib = await loadPdfJs();
  const buffer = await file.arrayBuffer();
  const pdf = await pdfjsLib.getDocument({ data: buffer }).promise;
  let fullText = "";
  for (let i = 1; i <= pdf.numPages; i++) {
    const page = await pdf.getPage(i);
    const content = await page.getTextContent();
    const pageText = content.items.map((it) => it.str).join(" ");
    fullText += pageText + "\n";
  }
  return fullText;
}

// Repère les lignes du type "Nom de la parcelle ... 12,4 ha" (ou "12.4 ha", "12,4ha", etc.)
function parseParcellesFromText(text) {
  const lines = text.split(/\n|(?=\b\d+[.,]?\d*\s*ha\b)/i);
  const found = [];
  const seen = new Set();
  const re = /^(.*?)[\s:.\-–]{0,4}(\d{1,4}(?:[.,]\d{1,2})?)\s*ha\b/i;
  lines.forEach((raw) => {
    const line = raw.trim().replace(/\s{2,}/g, " ");
    if (!line) return;
    const m = line.match(re);
    if (!m) return;
    let nom = m[1].trim().replace(/^[-•·\d.\s]+/, "").trim();
    const surface = parseFloat(m[2].replace(",", "."));
    if (!nom || !Number.isFinite(surface) || surface <= 0 || surface > 2000) return;
    // Évite les doublons exacts nom+surface
    const key = `${nom.toLowerCase()}|${surface}`;
    if (seen.has(key)) return;
    seen.add(key);
    found.push({ id: uid(), nom, surface, type: "cereale", culture: "" });
  });
  return found;
}

// ---------- OCR de bons de livraison (photo) — utilisé par le module Phytosanitaire ----------
let _tesseractLoadingPromise = null;
function loadTesseract() {
  if (window.Tesseract) return Promise.resolve(window.Tesseract);
  if (_tesseractLoadingPromise) return _tesseractLoadingPromise;
  _tesseractLoadingPromise = new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.src = "https://cdnjs.cloudflare.com/ajax/libs/tesseract.js/4.1.1/tesseract.min.js";
    script.onload = () => resolve(window.Tesseract);
    script.onerror = () => reject(new Error("Impossible de charger le lecteur de texte."));
    document.head.appendChild(script);
  });
  return _tesseractLoadingPromise;
}

async function extractImageText(file) {
  const Tesseract = await loadTesseract();
  const { data } = await Tesseract.recognize(file, "fra");
  return data.text || "";
}

function parseBonLivraisonFromText(text) {
  const lines = text.split("\n");
  const found = [];
  const seen = new Set();
  const re = /^(.{2,60}?)[\s:.\-–]{0,4}(\d{1,5}(?:[.,]\d{1,2})?)\s*(kg|l|u|unités?|unit(?:é|e)s?)?\s*$/i;
  lines.forEach((raw) => {
    const line = raw.trim().replace(/\s{2,}/g, " ");
    if (!line || line.length < 4) return;
    const m = line.match(re);
    if (!m) return;
    let nom = m[1].trim().replace(/^[-•·\d.\s]+/, "").trim();
    const quantite = parseFloat(m[2].replace(",", "."));
    if (!nom || nom.length < 2 || !Number.isFinite(quantite) || quantite <= 0 || quantite > 100000) return;
    if (/^(total|date|n°|numero|num[ée]ro|bon|client|page)\b/i.test(nom)) return;
    const key = `${nom.toLowerCase()}|${quantite}`;
    if (seen.has(key)) return;
    seen.add(key);
    found.push({ id: uid(), produit: nom, quantite });
  });
  return found;
}

function BonLivraisonImport({ onValider, tone = "admin" }) {
  const [status, setStatus] = useState("idle");
  const [error, setError] = useState("");
  const [fileName, setFileName] = useState("");
  const [extraites, setExtraites] = useState([]);

  async function handleChange(e) {
    const file = e.target.files && e.target.files[0];
    if (!file) return;
    setFileName(file.name || "photo");
    setStatus("lecture");
    setError("");
    try {
      const text = await extractImageText(file);
      const trouvees = parseBonLivraisonFromText(text);
      if (trouvees.length === 0) {
        setStatus("erreur");
        setError("Aucun produit reconnu sur cette photo. Vérifiez que le bon est bien net et bien cadré, ou ajoutez les produits à la main.");
        return;
      }
      setExtraites(trouvees);
      setStatus("revue");
    } catch (err) {
      setStatus("erreur");
      setError("La lecture de la photo a échoué. Réessayez avec une photo plus nette, ou ajoutez les produits à la main.");
    }
  }

  function updateExtraite(id, patch) {
    setExtraites((list) => list.map((p) => (p.id === id ? { ...p, ...patch } : p)));
  }
  function removeExtraite(id) {
    setExtraites((list) => list.filter((p) => p.id !== id));
  }
  function valider() {
    const valides = extraites.filter((p) => p.produit.trim() && p.quantite > 0);
    if (valides.length === 0) return;
    onValider(valides);
    setExtraites([]);
    setStatus("idle");
    setFileName("");
  }
  function annuler() {
    setExtraites([]);
    setStatus("idle");
    setFileName("");
    setError("");
  }

  const btnTone = tone === "admin" ? "bg-[#C97B3D] active:bg-[#9c5c29]" : "bg-[#1C2B1E] active:bg-[#0e1610]";

  return (
    <Card className="p-5 space-y-3">
      <div className="font-extrabold text-sm text-[#1C2B1E]/50">Photo du bon de livraison</div>
      <p className="text-xs text-[#1C2B1E]/45">Prenez en photo le bon de livraison du fournisseur. L'application essaie de reconnaître chaque produit et sa quantité.</p>

      {status !== "revue" && (
        <label className={`block w-full text-center text-sm font-bold text-white ${btnTone} rounded-2xl py-4 cursor-pointer`}>
          {status === "lecture" ? "Lecture de la photo…" : "📷 Prendre ou choisir une photo"}
          <input type="file" accept="image/*" capture="environment" className="hidden" onChange={handleChange} disabled={status === "lecture"} />
        </label>
      )}

      {fileName && status !== "revue" && <p className="text-xs text-[#1C2B1E]/45 text-center">{fileName}</p>}

      {status === "erreur" && <p className="text-sm text-[#D6483A] font-bold text-center">{error}</p>}

      {status === "revue" && (
        <div className="space-y-3">
          <p className="text-sm font-bold text-[#4A7C3F]">
            {extraites.length} produit{extraites.length > 1 ? "s" : ""} trouvé{extraites.length > 1 ? "s" : ""} sur la photo. Vérifiez et corrigez si besoin avant de valider.
          </p>
          <div className="space-y-2">
            {extraites.map((p) => (
              <Card key={p.id} className="p-3 !bg-[#F5F0E6]/50">
                <div className="flex gap-2">
                  <BigInput value={p.produit} onChange={(e) => updateExtraite(p.id, { produit: e.target.value })} placeholder="Produit" className="text-sm text-left py-2.5 flex-1" />
                  <BigInput type="number" step="0.01" inputMode="decimal" value={p.quantite} onChange={(e) => updateExtraite(p.id, { quantite: parseFloat(e.target.value) || 0 })} placeholder="Qté" className="text-sm py-2.5 !w-24" />
                  <button onClick={() => removeExtraite(p.id)} className="w-10 h-10 rounded-xl bg-[#D6483A]/10 text-[#D6483A] font-bold flex-shrink-0">✕</button>
                </div>
              </Card>
            ))}
            {extraites.length === 0 && <p className="text-center text-sm text-[#1C2B1E]/40 py-4">Toutes les lignes ont été retirées.</p>}
          </div>
          <ActionButton tone={tone} onClick={valider} disabled={extraites.length === 0}>
            Ajouter {extraites.length} produit{extraites.length > 1 ? "s" : ""} au stock
          </ActionButton>
          <button className="block w-full text-center text-sm font-bold text-[#1C2B1E]/40 py-1" onClick={annuler}>Annuler</button>
        </div>
      )}
    </Card>
  );
}

function StockInventaire({ produitsTheoriques, onValiderInventaire, tone = "admin" }) {
  const [comptes, setComptes] = useState({});
  const [valide, setValide] = useState(false);

  function setCompte(produit, val) {
    setComptes((c) => ({ ...c, [produit]: val }));
    setValide(false);
  }

  const lignes = produitsTheoriques.map((p) => {
    const saisie = comptes[p.produit];
    const compte = saisie !== undefined && saisie !== "" ? parseFloat(saisie) : null;
    const ecart = compte !== null ? compte - p.theorique : null;
    return { ...p, compte, ecart };
  });
  const toutesSaisies = lignes.length > 0 && lignes.every((l) => l.compte !== null);

  function validerInventaire() {
    if (!toutesSaisies) return;
    onValiderInventaire(lignes);
    setValide(true);
  }

  return (
    <Card className="p-5 space-y-3">
      <div className="font-extrabold text-sm text-[#1C2B1E]/50">Check des stocks — comptage physique</div>
      <p className="text-xs text-[#1C2B1E]/45">Comptez chaque produit sur place et entrez la quantité trouvée. L'écart avec le stock théorique s'affiche automatiquement.</p>

      {lignes.length === 0 && <p className="text-center text-sm text-[#1C2B1E]/40 py-6">Aucun produit en stock pour le moment.</p>}

      <div className="space-y-2">
        {lignes.map((l) => (
          <Card key={l.produit} className="p-3 !bg-[#F5F0E6]/50">
            <div className="flex items-center justify-between gap-2">
              <div className="flex-1">
                <div className="font-bold text-sm">{l.produit}</div>
                <div className="text-xs text-[#1C2B1E]/45">Stock théorique : {fmt(l.theorique, 1)}</div>
              </div>
              <BigInput
                type="number"
                step="0.01"
                inputMode="decimal"
                value={comptes[l.produit] ?? ""}
                onChange={(e) => setCompte(l.produit, e.target.value)}
                placeholder="Compté"
                className="text-sm py-2.5 !w-24"
              />
            </div>
            {l.compte !== null && l.ecart !== 0 && (
              <p className={`text-xs font-bold mt-2 ${l.ecart > 0 ? "text-[#4A7C3F]" : "text-[#D6483A]"}`}>
                Écart : {l.ecart > 0 ? "+" : ""}{fmt(l.ecart, 1)}
              </p>
            )}
            {l.compte !== null && l.ecart === 0 && <p className="text-xs font-bold mt-2 text-[#4A7C3F]">✓ Stock conforme</p>}
          </Card>
        ))}
      </div>

      {lignes.length > 0 && (
        <>
          {valide ? (
            <p className="text-sm font-extrabold text-[#4A7C3F] text-center">✓ Inventaire validé et enregistré</p>
          ) : (
            <ActionButton tone={tone} onClick={validerInventaire} disabled={!toutesSaisies}>
              Valider l'inventaire
            </ActionButton>
          )}
        </>
      )}
    </Card>
  );
}

function ParcellesModule({ parcelles, setParcelles, onBack }) {
  const [mode, setMode] = useState("manuel"); // manuel | pdf
  const [nom, setNom] = useState("");
  const [surface, setSurface] = useState("");
  const [type, setType] = useState("cereale");

  // État de l'import PDF
  const [pdfStatus, setPdfStatus] = useState("idle"); // idle | lecture | revue | erreur
  const [pdfError, setPdfError] = useState("");
  const [pdfFileName, setPdfFileName] = useState("");
  const [extraites, setExtraites] = useState([]); // parcelles trouvées, modifiables avant validation

  function addParcelle() {
    if (!nom.trim() || !surface) return;
    setParcelles((p) => [...p, { id: uid(), nom: nom.trim(), surface: parseFloat(surface), type, culture: type === "herbe" ? "Herbe" : "" }]);
    setNom("");
    setSurface("");
    setType("cereale");
  }
  function removeParcelle(id) {
    setParcelles((p) => p.filter((x) => x.id !== id));
  }
  const totalSurface = parcelles.reduce((s, p) => s + p.surface, 0);

  async function handlePdfChange(e) {
    const file = e.target.files && e.target.files[0];
    if (!file) return;
    setPdfFileName(file.name);
    setPdfStatus("lecture");
    setPdfError("");
    try {
      const text = await extractPdfText(file);
      const trouvees = parseParcellesFromText(text);
      if (trouvees.length === 0) {
        setPdfStatus("erreur");
        setPdfError("Aucune parcelle reconnue dans ce PDF. Le document doit contenir des lignes du type « Nom de la parcelle … 12,4 ha ». Vous pouvez les ajouter à la main ci-dessous.");
        return;
      }
      setExtraites(trouvees);
      setPdfStatus("revue");
    } catch (err) {
      setPdfStatus("erreur");
      setPdfError("La lecture du PDF a échoué. Vérifiez que le fichier n'est pas un scan/image et réessayez, ou ajoutez les parcelles à la main.");
    }
  }

  function updateExtraite(id, patch) {
    setExtraites((list) => list.map((p) => (p.id === id ? { ...p, ...patch } : p)));
  }
  function removeExtraite(id) {
    setExtraites((list) => list.filter((p) => p.id !== id));
  }
  function validerImport() {
    const valides = extraites.filter((p) => p.nom.trim() && p.surface > 0);
    if (valides.length === 0) return;
    setParcelles((p) => [...p, ...valides.map((p2) => ({ ...p2, culture: p2.type === "herbe" ? "Herbe" : "" }))]);
    setExtraites([]);
    setPdfStatus("idle");
    setPdfFileName("");
  }
  function annulerImport() {
    setExtraites([]);
    setPdfStatus("idle");
    setPdfFileName("");
    setPdfError("");
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Parcelles" onBack={onBack} tone="admin" />
      <div className="p-5 space-y-4">
        <div className="flex gap-3">
          <BigStat label="Parcelles" value={parcelles.length} tone="admin" />
          <BigStat label="Surface" value={`${fmt(totalSurface, 1)} ha`} tone="admin" />
        </div>

        <PillChoice
          tone="admin"
          columns={2}
          value={mode}
          onChange={(m) => { setMode(m); if (m === "manuel") annulerImport(); }}
          options={[{ value: "manuel", label: "Saisie manuelle" }, { value: "pdf", label: "Importer un PDF" }]}
        />

        {mode === "manuel" && (
          <Card className="p-5 space-y-3">
            <div className="font-extrabold text-sm text-[#1C2B1E]/50">Ajouter une parcelle</div>
            <BigInput value={nom} onChange={(e) => setNom(e.target.value)} placeholder="Nom de la parcelle" className="text-base text-left" />
            <BigInput type="number" step="0.01" inputMode="decimal" value={surface} onChange={(e) => setSurface(e.target.value)} placeholder="Surface (ha)" />
            <PillChoice
              tone="admin"
              columns={2}
              value={type}
              onChange={setType}
              options={[{ value: "cereale", label: "Champ" }, { value: "herbe", label: "Prairie" }]}
            />
            <ActionButton tone="admin" onClick={addParcelle} disabled={!nom.trim() || !surface}>+ Ajouter</ActionButton>
          </Card>
        )}

        {mode === "pdf" && (
          <Card className="p-5 space-y-3">
            <div className="font-extrabold text-sm text-[#1C2B1E]/50">Importer un PDF de parcelles</div>
            <p className="text-xs text-[#1C2B1E]/45">Le document doit contenir une ligne par parcelle avec son nom et sa surface en hectares (ex : « La Grande Pièce — 8,4 ha »).</p>

            {pdfStatus !== "revue" && (
              <label className="block w-full text-center text-sm font-bold text-white bg-[#C97B3D] active:bg-[#9c5c29] rounded-2xl py-4 cursor-pointer">
                {pdfStatus === "lecture" ? "Lecture du fichier…" : "📄 Choisir un fichier PDF"}
                <input type="file" accept="application/pdf" className="hidden" onChange={handlePdfChange} disabled={pdfStatus === "lecture"} />
              </label>
            )}

            {pdfFileName && pdfStatus !== "revue" && (
              <p className="text-xs text-[#1C2B1E]/45 text-center">{pdfFileName}</p>
            )}

            {pdfStatus === "erreur" && (
              <p className="text-sm text-[#D6483A] font-bold text-center">{pdfError}</p>
            )}

            {pdfStatus === "revue" && (
              <div className="space-y-3">
                <p className="text-sm font-bold text-[#4A7C3F]">
                  {extraites.length} parcelle{extraites.length > 1 ? "s" : ""} trouvée{extraites.length > 1 ? "s" : ""} dans « {pdfFileName} ». Vérifiez et corrigez si besoin avant de valider.
                </p>
                <div className="space-y-2">
                  {extraites.map((p) => (
                    <Card key={p.id} className="p-3 space-y-2 !bg-[#F5F0E6]/50">
                      <div className="flex gap-2">
                        <BigInput value={p.nom} onChange={(e) => updateExtraite(p.id, { nom: e.target.value })} placeholder="Nom" className="text-sm text-left py-2.5 flex-1" />
                        <BigInput type="number" step="0.01" inputMode="decimal" value={p.surface} onChange={(e) => updateExtraite(p.id, { surface: parseFloat(e.target.value) || 0 })} placeholder="ha" className="text-sm py-2.5 !w-24" />
                        <button onClick={() => removeExtraite(p.id)} className="w-10 h-10 rounded-xl bg-[#D6483A]/10 text-[#D6483A] font-bold flex-shrink-0">✕</button>
                      </div>
                      <PillChoice
                        tone="admin"
                        columns={2}
                        value={p.type}
                        onChange={(t) => updateExtraite(p.id, { type: t })}
                        options={[{ value: "cereale", label: "Champ" }, { value: "herbe", label: "Prairie" }]}
                      />
                    </Card>
                  ))}
                  {extraites.length === 0 && <p className="text-center text-sm text-[#1C2B1E]/40 py-4">Toutes les lignes ont été retirées.</p>}
                </div>
                <ActionButton tone="admin" onClick={validerImport} disabled={extraites.length === 0}>
                  Ajouter {extraites.length} parcelle{extraites.length > 1 ? "s" : ""}
                </ActionButton>
                <button className="block w-full text-center text-sm font-bold text-[#1C2B1E]/40 py-1" onClick={annulerImport}>Annuler l'import</button>
              </div>
            )}
          </Card>
        )}

        <div className="space-y-2">
          {parcelles.map((p) => (
            <Card key={p.id} className="p-4 flex items-center justify-between">
              <div>
                <div className="font-bold">{p.nom}</div>
                <div className="text-sm text-[#1C2B1E]/50">{fmt(p.surface, 1)} ha · {p.culture || (p.type === "herbe" ? "Prairie" : "Champ")}</div>
              </div>
              <button onClick={() => removeParcelle(p.id)} className="w-9 h-9 rounded-full bg-[#D6483A]/10 text-[#D6483A] font-bold flex items-center justify-center active:bg-[#D6483A]/20">✕</button>
            </Card>
          ))}
          {parcelles.length === 0 && <p className="text-center text-sm text-[#1C2B1E]/40 py-8">Aucune parcelle pour le moment.</p>}
        </div>
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: ENSILAGE — ADMIN (création toujours visible)
   ============================================================ */
function EnsilageAdminModule({ parcelles, chantiers, setChantiers, onBack }) {
  const [nomChantier, setNomChantier] = useState("");
  const [selected, setSelected] = useState([]);
  const [openId, setOpenId] = useState(chantiers[0]?.id || null);

  function createChantier() {
    if (!nomChantier.trim() || selected.length === 0) return;
    const c = { id: uid(), nom: nomChantier.trim(), parcelleIds: selected, statut: "ouvert", pesees: [], createdAt: todayISO() };
    setChantiers((cs) => [c, ...cs]);
    setOpenId(c.id);
    setNomChantier("");
    setSelected([]);
  }
  function closeChantier(id) {
    setChantiers((cs) => cs.map((c) => (c.id === id ? { ...c, statut: "fermé" } : c)));
  }
  const chantier = chantiers.find((c) => c.id === openId);

  function exportPdf(c) {
    const byParcelle = {};
    let total = 0;
    c.pesees.forEach((p) => { byParcelle[p.parcelleId] = (byParcelle[p.parcelleId] || 0) + p.net; total += p.net; });
    const rows = c.parcelleIds.map((pid) => {
      const parc = parcelles.find((x) => x.id === pid);
      return `<tr><td>${parc?.nom}</td><td>${fmt(parc?.surface || 0, 1)} ha</td><td>${fmt(byParcelle[pid] || 0, 0)} kg</td></tr>`;
    }).join("");
    openPdfWindow(`Chantier ensilage — ${c.nom}`,
      `<div class="meta">Date : ${c.createdAt}</div><table><thead><tr><th>Parcelle</th><th>Surface</th><th>Poids net</th></tr></thead><tbody>${rows}</tbody>
       <tfoot><tr class="total-row"><td colspan="2">TOTAL SILO</td><td>${fmt(total, 0)} kg</td></tr></tfoot></table>`);
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Ensilage" onBack={onBack} tone="admin" />
      <div className="p-5 space-y-4">
        <Card className="p-5 space-y-3">
          <div className="font-extrabold text-sm text-[#1C2B1E]/50">Ouvrir un chantier</div>
          <BigInput value={nomChantier} onChange={(e) => setNomChantier(e.target.value)} placeholder="Nom du chantier" className="text-base text-left" />
          <div className="flex flex-wrap gap-2">
            {parcelles.map((p) => (
              <button
                key={p.id}
                onClick={() => setSelected((s) => (s.includes(p.id) ? s.filter((x) => x !== p.id) : [...s, p.id]))}
                className={`px-4 py-3 rounded-xl text-sm font-bold ${selected.includes(p.id) ? "bg-[#C97B3D] text-white" : "bg-[#F5F0E6] text-[#1C2B1E]/70"}`}
              >
                {p.nom}
              </button>
            ))}
          </div>
          <ActionButton tone="admin" onClick={createChantier} disabled={!nomChantier.trim() || selected.length === 0}>+ Créer le chantier</ActionButton>
        </Card>

        {chantiers.length > 0 && (
          <div className="flex gap-2 overflow-x-auto pb-1">
            {chantiers.map((c) => (
              <button
                key={c.id}
                onClick={() => setOpenId(c.id)}
                className={`px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap ${openId === c.id ? "bg-[#1C2B1E] text-white" : "bg-white text-[#1C2B1E]/60"}`}
              >
                {c.nom} {c.statut === "fermé" && "✓"}
              </button>
            ))}
          </div>
        )}

        {chantier && (() => {
          const byParcelle = {};
          let total = 0;
          chantier.pesees.forEach((p) => { byParcelle[p.parcelleId] = (byParcelle[p.parcelleId] || 0) + p.net; total += p.net; });
          return (
            <Card className="p-5">
              <div className="flex items-center justify-between mb-3">
                <div className="font-extrabold text-lg">{chantier.nom}</div>
                <span className={`text-xs font-bold px-2 py-1 rounded-full ${chantier.statut === "ouvert" ? "bg-[#4A7C3F]/15 text-[#4A7C3F]" : "bg-[#1C2B1E]/10"}`}>
                  {chantier.statut.toUpperCase()}
                </span>
              </div>
              <div className="space-y-1.5 mb-3">
                {chantier.parcelleIds.map((pid) => {
                  const p = parcelles.find((x) => x.id === pid);
                  return (
                    <div key={pid} className="flex justify-between text-sm py-1 border-b border-[#1C2B1E]/5">
                      <span>{p?.nom}</span>
                      <span className="font-bold">{fmt(byParcelle[pid] || 0, 0)} kg</span>
                    </div>
                  );
                })}
              </div>
              <div className="flex justify-between font-extrabold text-xl mb-4">
                <span>Total silo</span>
                <span className="text-[#C97B3D]">{fmt(total, 0)} kg</span>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <ActionButton tone="ghost" size="md" onClick={() => exportPdf(chantier)}>📄 Export PDF</ActionButton>
                {chantier.statut === "ouvert" && <ActionButton tone="ghost" size="md" onClick={() => closeChantier(chantier.id)}>Fermer</ActionButton>}
              </div>
            </Card>
          );
        })()}
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: ENSILAGE — CHAUFFEUR (tare + poids + valider, rien d'autre)
   ============================================================ */
function EnsilageDriverModule({ chantiers, setChantiers, parcelles, driverName, onBack }) {
  const open = chantiers.filter((c) => c.statut === "ouvert");
  const [activeId, setActiveId] = useState(open[0]?.id || null);
  const chantier = chantiers.find((c) => c.id === activeId);
  const [parcelleId, setParcelleId] = useState(chantier?.parcelleIds[0] || "");
  const [tare, setTare] = useState("");
  const [brut, setBrut] = useState("");
  const [confirm, setConfirm] = useState(false);

  const mine = (chantier?.pesees || []).filter((p) => p.chauffeur === driverName);
  const totalMine = mine.reduce((s, p) => s + p.net, 0);
  const net = tare && brut ? Math.max(0, parseFloat(brut) - parseFloat(tare)) : 0;

  function valider() {
    if (!tare || !brut || !chantier || !parcelleId) return;
    setChantiers((cs) => cs.map((c) => c.id === chantier.id
      ? { ...c, pesees: [...c.pesees, { id: uid(), parcelleId, chauffeur: driverName, tare: parseFloat(tare), brut: parseFloat(brut), net }] }
      : c));
    setBrut("");
    setTare("");
    setConfirm(true);
    setTimeout(() => setConfirm(false), 1500);
  }

  if (open.length === 0) {
    return (
      <div className="screen-in min-h-screen bg-[#F5F0E6]">
        <ScreenHeader title="Ensilage" onBack={onBack} tone="driver" />
        <p className="text-center text-base text-[#1C2B1E]/45 py-16 px-6">Aucun chantier ouvert pour le moment. Demandez à l'administrateur d'en ouvrir un.</p>
      </div>
    );
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Ensilage" onBack={onBack} tone="driver" />
      <div className="p-5 space-y-4">
        {open.length > 1 && (
          <div className="flex gap-2 overflow-x-auto">
            {open.map((c) => (
              <button
                key={c.id}
                onClick={() => { setActiveId(c.id); setParcelleId(c.parcelleIds[0] || ""); }}
                className={`px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap ${activeId === c.id ? "bg-[#1C2B1E] text-white" : "bg-white text-[#1C2B1E]/60"}`}
              >
                {c.nom}
              </button>
            ))}
          </div>
        )}

        <Card className="p-5 space-y-4">
          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Parcelle</div>
            <div className="flex flex-wrap gap-2">
              {chantier?.parcelleIds.map((pid) => {
                const p = parcelles.find((x) => x.id === pid);
                return (
                  <button
                    key={pid}
                    onClick={() => setParcelleId(pid)}
                    className={`px-4 py-3 rounded-xl text-sm font-bold ${parcelleId === pid ? "bg-[#1C2B1E] text-white" : "bg-[#F5F0E6] text-[#1C2B1E]/70"}`}
                  >
                    {p?.nom}
                  </button>
                );
              })}
            </div>
          </div>

          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Tare (kg)</div>
            <BigInput type="number" inputMode="decimal" value={tare} onChange={(e) => setTare(e.target.value)} placeholder="0" />
          </div>
          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Poids en charge (kg)</div>
            <BigInput type="number" inputMode="decimal" value={brut} onChange={(e) => setBrut(e.target.value)} placeholder="0" />
          </div>

          {net > 0 && (
            <div className="text-center bg-[#4A7C3F]/10 rounded-2xl py-3">
              <div className="text-xs font-bold text-[#4A7C3F]/70 uppercase">Poids net</div>
              <div className="text-3xl font-extrabold text-[#4A7C3F]">{fmt(net, 0)} kg</div>
            </div>
          )}

          <ActionButton tone="driver" onClick={valider} disabled={!tare || !brut}>
            {confirm ? "✓ Enregistré !" : "Valider la pesée"}
          </ActionButton>
        </Card>

        <Card className="p-5 !bg-[#1C2B1E] text-white text-center">
          <div className="text-sm opacity-60">Mon total aujourd'hui</div>
          <div className="text-4xl font-extrabold mt-1">{fmt(totalMine, 0)} kg</div>
          <div className="text-xs opacity-45 mt-1">{mine.length} pesée(s)</div>
        </Card>
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: ÉPANDAGE — ADMIN (création toujours visible)
   ============================================================ */
function EpandageAdminModule({ parcelles, chantiers, setChantiers, onBack }) {
  const [nomChantier, setNomChantier] = useState("");
  const [selected, setSelected] = useState([]);
  const [openId, setOpenId] = useState(chantiers[0]?.id || null);

  function createChantier() {
    if (!nomChantier.trim() || selected.length === 0) return;
    const c = { id: uid(), nom: nomChantier.trim(), parcelleIds: selected, statut: "ouvert", pesees: [], createdAt: todayISO() };
    setChantiers((cs) => [c, ...cs]);
    setOpenId(c.id);
    setNomChantier("");
    setSelected([]);
  }
  function closeChantier(id) {
    setChantiers((cs) => cs.map((c) => (c.id === id ? { ...c, statut: "fermé" } : c)));
  }
  const chantier = chantiers.find((c) => c.id === openId);

  function exportPdf(c) {
    const byParcelle = {};
    let total = 0;
    c.pesees.forEach((p) => { byParcelle[p.parcelleId] = (byParcelle[p.parcelleId] || 0) + p.net; total += p.net; });
    const rows = c.parcelleIds.map((pid) => {
      const parc = parcelles.find((x) => x.id === pid);
      return `<tr><td>${parc?.nom}</td><td>${fmt(parc?.surface || 0, 1)} ha</td><td>${fmt(byParcelle[pid] || 0, 0)} kg</td></tr>`;
    }).join("");
    openPdfWindow(`Chantier épandage — ${c.nom}`,
      `<div class="meta">Date : ${c.createdAt}</div><table><thead><tr><th>Parcelle</th><th>Surface</th><th>Poids net</th></tr></thead><tbody>${rows}</tbody>
       <tfoot><tr class="total-row"><td colspan="2">TOTAL ÉPANDU</td><td>${fmt(total, 0)} kg</td></tr></tfoot></table>`);
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Épandage" onBack={onBack} tone="admin" />
      <div className="p-5 space-y-4">
        <Card className="p-5 space-y-3">
          <div className="font-extrabold text-sm text-[#1C2B1E]/50">Ouvrir un chantier</div>
          <BigInput value={nomChantier} onChange={(e) => setNomChantier(e.target.value)} placeholder="Nom du chantier" className="text-base text-left" />
          <div className="flex flex-wrap gap-2">
            {parcelles.map((p) => (
              <button
                key={p.id}
                onClick={() => setSelected((s) => (s.includes(p.id) ? s.filter((x) => x !== p.id) : [...s, p.id]))}
                className={`px-4 py-3 rounded-xl text-sm font-bold ${selected.includes(p.id) ? "bg-[#C97B3D] text-white" : "bg-[#F5F0E6] text-[#1C2B1E]/70"}`}
              >
                {p.nom}
              </button>
            ))}
          </div>
          <ActionButton tone="admin" onClick={createChantier} disabled={!nomChantier.trim() || selected.length === 0}>+ Créer le chantier</ActionButton>
        </Card>

        {chantiers.length > 0 && (
          <div className="flex gap-2 overflow-x-auto pb-1">
            {chantiers.map((c) => (
              <button
                key={c.id}
                onClick={() => setOpenId(c.id)}
                className={`px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap ${openId === c.id ? "bg-[#1C2B1E] text-white" : "bg-white text-[#1C2B1E]/60"}`}
              >
                {c.nom} {c.statut === "fermé" && "✓"}
              </button>
            ))}
          </div>
        )}

        {chantier && (() => {
          const byParcelle = {};
          let total = 0;
          chantier.pesees.forEach((p) => { byParcelle[p.parcelleId] = (byParcelle[p.parcelleId] || 0) + p.net; total += p.net; });
          return (
            <Card className="p-5">
              <div className="flex items-center justify-between mb-3">
                <div className="font-extrabold text-lg">{chantier.nom}</div>
                <span className={`text-xs font-bold px-2 py-1 rounded-full ${chantier.statut === "ouvert" ? "bg-[#4A7C3F]/15 text-[#4A7C3F]" : "bg-[#1C2B1E]/10"}`}>
                  {chantier.statut.toUpperCase()}
                </span>
              </div>
              <div className="space-y-1.5 mb-3">
                {chantier.parcelleIds.map((pid) => {
                  const p = parcelles.find((x) => x.id === pid);
                  return (
                    <div key={pid} className="flex justify-between text-sm py-1 border-b border-[#1C2B1E]/5">
                      <span>{p?.nom}</span>
                      <span className="font-bold">{fmt(byParcelle[pid] || 0, 0)} kg</span>
                    </div>
                  );
                })}
              </div>
              <div className="flex justify-between font-extrabold text-xl mb-4">
                <span>Total épandu</span>
                <span className="text-[#C97B3D]">{fmt(total, 0)} kg</span>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <ActionButton tone="ghost" size="md" onClick={() => exportPdf(chantier)}>📄 Export PDF</ActionButton>
                {chantier.statut === "ouvert" && <ActionButton tone="ghost" size="md" onClick={() => closeChantier(chantier.id)}>Fermer</ActionButton>}
              </div>
            </Card>
          );
        })()}
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: ÉPANDAGE — CHAUFFEUR (tare + poids + valider, rien d'autre)
   ============================================================ */
function EpandageDriverModule({ chantiers, setChantiers, parcelles, driverName, onBack }) {
  const open = chantiers.filter((c) => c.statut === "ouvert");
  const [activeId, setActiveId] = useState(open[0]?.id || null);
  const chantier = chantiers.find((c) => c.id === activeId);
  const [parcelleId, setParcelleId] = useState(chantier?.parcelleIds[0] || "");
  const [tare, setTare] = useState("");
  const [brut, setBrut] = useState("");
  const [confirm, setConfirm] = useState(false);

  const mine = (chantier?.pesees || []).filter((p) => p.chauffeur === driverName);
  const totalMine = mine.reduce((s, p) => s + p.net, 0);
  const net = tare && brut ? Math.max(0, parseFloat(brut) - parseFloat(tare)) : 0;

  function valider() {
    if (!tare || !brut || !chantier || !parcelleId) return;
    setChantiers((cs) => cs.map((c) => c.id === chantier.id
      ? { ...c, pesees: [...c.pesees, { id: uid(), parcelleId, chauffeur: driverName, tare: parseFloat(tare), brut: parseFloat(brut), net }] }
      : c));
    setBrut("");
    setTare("");
    setConfirm(true);
    setTimeout(() => setConfirm(false), 1500);
  }

  if (open.length === 0) {
    return (
      <div className="screen-in min-h-screen bg-[#F5F0E6]">
        <ScreenHeader title="Épandage" onBack={onBack} tone="driver" />
        <p className="text-center text-base text-[#1C2B1E]/45 py-16 px-6">Aucun chantier ouvert pour le moment. Demandez à l'administrateur d'en ouvrir un.</p>
      </div>
    );
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Épandage" onBack={onBack} tone="driver" />
      <div className="p-5 space-y-4">
        {open.length > 1 && (
          <div className="flex gap-2 overflow-x-auto">
            {open.map((c) => (
              <button
                key={c.id}
                onClick={() => { setActiveId(c.id); setParcelleId(c.parcelleIds[0] || ""); }}
                className={`px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap ${activeId === c.id ? "bg-[#1C2B1E] text-white" : "bg-white text-[#1C2B1E]/60"}`}
              >
                {c.nom}
              </button>
            ))}
          </div>
        )}

        <Card className="p-5 space-y-4">
          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Parcelle</div>
            <div className="flex flex-wrap gap-2">
              {chantier?.parcelleIds.map((pid) => {
                const p = parcelles.find((x) => x.id === pid);
                return (
                  <button
                    key={pid}
                    onClick={() => setParcelleId(pid)}
                    className={`px-4 py-3 rounded-xl text-sm font-bold ${parcelleId === pid ? "bg-[#1C2B1E] text-white" : "bg-[#F5F0E6] text-[#1C2B1E]/70"}`}
                  >
                    {p?.nom}
                  </button>
                );
              })}
            </div>
          </div>

          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Tare (kg)</div>
            <BigInput type="number" inputMode="decimal" value={tare} onChange={(e) => setTare(e.target.value)} placeholder="0" />
          </div>
          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Poids en charge (kg)</div>
            <BigInput type="number" inputMode="decimal" value={brut} onChange={(e) => setBrut(e.target.value)} placeholder="0" />
          </div>

          {net > 0 && (
            <div className="text-center bg-[#4A7C3F]/10 rounded-2xl py-3">
              <div className="text-xs font-bold text-[#4A7C3F]/70 uppercase">Poids net</div>
              <div className="text-3xl font-extrabold text-[#4A7C3F]">{fmt(net, 0)} kg</div>
            </div>
          )}

          <ActionButton tone="driver" onClick={valider} disabled={!tare || !brut}>
            {confirm ? "✓ Enregistré !" : "Valider la pesée"}
          </ActionButton>
        </Card>

        <Card className="p-5 !bg-[#1C2B1E] text-white text-center">
          <div className="text-sm opacity-60">Mon total aujourd'hui</div>
          <div className="text-4xl font-extrabold mt-1">{fmt(totalMine, 0)} kg</div>
          <div className="text-xs opacity-45 mt-1">{mine.length} pesée(s)</div>
        </Card>
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: MOISSON — ADMIN (création toujours visible)
   ============================================================ */
function MoissonAdminModule({ parcelles, chantiers, setChantiers, onBack }) {
  const [nomChantier, setNomChantier] = useState("");
  const [cereale, setCereale] = useState("Blé tendre");
  const [selected, setSelected] = useState([]);
  const [openId, setOpenId] = useState(chantiers[0]?.id || null);
  const cereales = ["Blé tendre", "Orge", "Colza", "Tournesol", "Maïs grain"];

  function createChantier() {
    if (!nomChantier.trim() || selected.length === 0) return;
    const c = { id: uid(), nom: nomChantier.trim(), cereale, parcelleIds: selected, statut: "ouvert", pesees: [], createdAt: todayISO() };
    setChantiers((cs) => [c, ...cs]);
    setOpenId(c.id);
    setNomChantier("");
    setSelected([]);
  }
  function closeChantier(id) {
    setChantiers((cs) => cs.map((c) => (c.id === id ? { ...c, statut: "fermé" } : c)));
  }
  const chantier = chantiers.find((c) => c.id === openId);

  function exportPdf(c) {
    const byParcelle = {};
    let total = 0;
    c.pesees.forEach((p) => { byParcelle[p.parcelleId] = (byParcelle[p.parcelleId] || 0) + p.net; total += p.net; });
    const rows = c.parcelleIds.map((pid) => {
      const parc = parcelles.find((x) => x.id === pid);
      const t = byParcelle[pid] || 0;
      const rendement = parc?.surface ? t / 1000 / parc.surface : 0;
      return `<tr><td>${parc?.nom}</td><td>${fmt(parc?.surface || 0, 1)} ha</td><td>${fmt(t, 0)} kg</td><td>${fmt(rendement, 2)} t/ha</td></tr>`;
    }).join("");
    openPdfWindow(`Chantier moisson — ${c.nom}`,
      `<div class="meta">${c.cereale} · ${c.createdAt}</div><table><thead><tr><th>Parcelle</th><th>Surface</th><th>Poids net</th><th>Rendement</th></tr></thead><tbody>${rows}</tbody>
       <tfoot><tr class="total-row"><td colspan="2">TOTAL</td><td>${fmt(total, 0)} kg</td><td></td></tr></tfoot></table>`);
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Moisson" onBack={onBack} tone="admin" />
      <div className="p-5 space-y-4">
        <Card className="p-5 space-y-3">
          <div className="font-extrabold text-sm text-[#1C2B1E]/50">Ouvrir un chantier</div>
          <BigInput value={nomChantier} onChange={(e) => setNomChantier(e.target.value)} placeholder="Nom du chantier" className="text-base text-left" />
          <PillChoice tone="admin" columns={2} value={cereale} onChange={setCereale} options={cereales.map((c) => ({ value: c, label: c }))} />
          <div className="flex flex-wrap gap-2">
            {parcelles.map((p) => (
              <button key={p.id} onClick={() => setSelected((s) => (s.includes(p.id) ? s.filter((x) => x !== p.id) : [...s, p.id]))}
                className={`px-4 py-3 rounded-xl text-sm font-bold ${selected.includes(p.id) ? "bg-[#C97B3D] text-white" : "bg-[#F5F0E6] text-[#1C2B1E]/70"}`}>
                {p.nom}
              </button>
            ))}
          </div>
          <ActionButton tone="admin" onClick={createChantier} disabled={!nomChantier.trim() || selected.length === 0}>+ Créer le chantier</ActionButton>
        </Card>

        {chantiers.length > 0 && (
          <div className="flex gap-2 overflow-x-auto pb-1">
            {chantiers.map((c) => (
              <button key={c.id} onClick={() => setOpenId(c.id)} className={`px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap ${openId === c.id ? "bg-[#1C2B1E] text-white" : "bg-white text-[#1C2B1E]/60"}`}>
                {c.nom} {c.statut === "fermé" && "✓"}
              </button>
            ))}
          </div>
        )}

        {chantier && (() => {
          let totalGeneral = 0;
          const lines = chantier.parcelleIds.map((pid) => {
            const p = parcelles.find((x) => x.id === pid);
            const pesees = chantier.pesees.filter((x) => x.parcelleId === pid);
            const total = pesees.reduce((s, x) => s + x.net, 0);
            totalGeneral += total;
            const rendement = p?.surface ? total / 1000 / p.surface : 0;
            return { p, total, rendement };
          });
          return (
            <Card className="p-5">
              <div className="flex items-center justify-between mb-3">
                <div>
                  <div className="font-extrabold text-lg">{chantier.nom}</div>
                  <div className="text-xs text-[#1C2B1E]/45">{chantier.cereale}</div>
                </div>
                <span className={`text-xs font-bold px-2 py-1 rounded-full ${chantier.statut === "ouvert" ? "bg-[#4A7C3F]/15 text-[#4A7C3F]" : "bg-[#1C2B1E]/10"}`}>{chantier.statut.toUpperCase()}</span>
              </div>
              <div className="space-y-1.5 mb-3">
                {lines.map(({ p, total, rendement }) => (
                  <div key={p.id} className="flex justify-between text-sm py-1 border-b border-[#1C2B1E]/5">
                    <span>{p.nom}</span>
                    <span className="font-bold">{fmt(total, 0)} kg · {fmt(rendement, 2)} t/ha</span>
                  </div>
                ))}
              </div>
              <div className="flex justify-between font-extrabold text-xl mb-4">
                <span>Total moisson</span><span className="text-[#C97B3D]">{fmt(totalGeneral, 0)} kg</span>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <ActionButton tone="ghost" size="md" onClick={() => exportPdf(chantier)}>📄 Export PDF</ActionButton>
                {chantier.statut === "ouvert" && <ActionButton tone="ghost" size="md" onClick={() => closeChantier(chantier.id)}>Fermer</ActionButton>}
              </div>
            </Card>
          );
        })()}
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: MOISSON — CHAUFFEUR (tout sur 1 seul écran, pas d'étapes)
   ============================================================ */
function MoissonDriverModule({ chantiers, setChantiers, parcelles, driverName, onBack }) {
  const open = chantiers.filter((c) => c.statut === "ouvert");
  const [activeId, setActiveId] = useState(open[0]?.id || null);
  const chantier = chantiers.find((c) => c.id === activeId);
  const [parcelleId, setParcelleId] = useState(chantier?.parcelleIds[0] || "");
  const [tare, setTare] = useState("");
  const [brut, setBrut] = useState("");
  const [ps, setPs] = useState("");
  const [temp, setTemp] = useState("");
  const [proteine, setProteine] = useState("");
  const [confirm, setConfirm] = useState(false);
  const [showDetails, setShowDetails] = useState(false);

  const mine = (chantier?.pesees || []).filter((p) => p.chauffeur === driverName);
  const totalMine = mine.reduce((s, p) => s + p.net, 0);
  const net = tare && brut ? Math.max(0, parseFloat(brut) - parseFloat(tare)) : 0;

  function valider() {
    if (!tare || !brut || !chantier) return;
    setChantiers((cs) => cs.map((c) => c.id === chantier.id
      ? { ...c, pesees: [...c.pesees, { id: uid(), parcelleId, chauffeur: driverName, tare: parseFloat(tare), brut: parseFloat(brut), net,
          poidsSpecifique: ps ? parseFloat(ps) : null, temperature: temp ? parseFloat(temp) : null, proteine: proteine ? parseFloat(proteine) : null }] }
      : c));
    setBrut(""); setTare(""); setPs(""); setTemp(""); setProteine("");
    setConfirm(true);
    setTimeout(() => setConfirm(false), 1500);
  }

  if (open.length === 0) {
    return (
      <div className="screen-in min-h-screen bg-[#F5F0E6]">
        <ScreenHeader title="Moisson" onBack={onBack} tone="driver" />
        <p className="text-center text-base text-[#1C2B1E]/45 py-16 px-6">Aucun chantier ouvert pour le moment.</p>
      </div>
    );
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Moisson" onBack={onBack} tone="driver" />
      <div className="p-5 space-y-4">
        {open.length > 1 && (
          <div className="flex gap-2 overflow-x-auto">
            {open.map((c) => (
              <button key={c.id} onClick={() => { setActiveId(c.id); setParcelleId(c.parcelleIds[0] || ""); }}
                className={`px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap ${activeId === c.id ? "bg-[#1C2B1E] text-white" : "bg-white text-[#1C2B1E]/60"}`}>
                {c.nom}
              </button>
            ))}
          </div>
        )}

        <Card className="p-5 space-y-4">
          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Parcelle</div>
            <div className="flex flex-wrap gap-2">
              {chantier?.parcelleIds.map((pid) => {
                const p = parcelles.find((x) => x.id === pid);
                return (
                  <button key={pid} onClick={() => setParcelleId(pid)} className={`px-4 py-3 rounded-xl text-sm font-bold ${parcelleId === pid ? "bg-[#1C2B1E] text-white" : "bg-[#F5F0E6] text-[#1C2B1E]/70"}`}>
                    {p?.nom}
                  </button>
                );
              })}
            </div>
          </div>

          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Tare (kg)</div>
            <BigInput type="number" inputMode="decimal" value={tare} onChange={(e) => setTare(e.target.value)} placeholder="0" />
          </div>
          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Poids en charge (kg)</div>
            <BigInput type="number" inputMode="decimal" value={brut} onChange={(e) => setBrut(e.target.value)} placeholder="0" />
          </div>
          {net > 0 && (
            <div className="text-center bg-[#4A7C3F]/10 rounded-2xl py-3">
              <div className="text-xs font-bold text-[#4A7C3F]/70 uppercase">Poids net</div>
              <div className="text-3xl font-extrabold text-[#4A7C3F]">{fmt(net, 0)} kg</div>
            </div>
          )}

          {!showDetails ? (
            <button className="block w-full text-center text-sm font-bold text-[#1C2B1E]/40 py-1" onClick={() => setShowDetails(true)}>
              + Ajouter PS / température / protéine (optionnel)
            </button>
          ) : (
            <div className="grid grid-cols-3 gap-2">
              <BigInput type="number" inputMode="decimal" value={ps} onChange={(e) => setPs(e.target.value)} placeholder="PS" className="text-sm py-3" />
              <BigInput type="number" inputMode="decimal" value={temp} onChange={(e) => setTemp(e.target.value)} placeholder="°C" className="text-sm py-3" />
              <BigInput type="number" inputMode="decimal" value={proteine} onChange={(e) => setProteine(e.target.value)} placeholder="Prot. %" className="text-sm py-3" />
            </div>
          )}

          <ActionButton tone="driver" onClick={valider} disabled={!tare || !brut}>
            {confirm ? "✓ Enregistré !" : "Valider la pesée"}
          </ActionButton>
        </Card>

        <Card className="p-5 !bg-[#1C2B1E] text-white text-center">
          <div className="text-sm opacity-60">Mon total aujourd'hui</div>
          <div className="text-4xl font-extrabold mt-1">{fmt(totalMine, 0)} kg</div>
          <div className="text-xs opacity-45 mt-1">{mine.length} pesée(s)</div>
        </Card>
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: PRESSAGE — ADMIN (création toujours visible)
   ============================================================ */
function PressageAdminModule({ parcelles, taches, setTaches, stock, setStock, onBack }) {
  const [nomTache, setNomTache] = useState("");
  const [typeBotte, setTypeBotte] = useState("Paille");
  const [selected, setSelected] = useState([]);
  const [openId, setOpenId] = useState(taches[0]?.id || null);
  const [closing, setClosing] = useState(false);
  const [bottesUtilisees, setBottesUtilisees] = useState("");

  const tache = taches.find((t) => t.id === openId);
  const stockTotal = stock.mouvements.reduce((s, m) => s + (m.type === "entrée" ? m.quantite : -m.quantite), 0);

  function createTache() {
    if (!nomTache.trim() || selected.length === 0) return;
    const t = { id: uid(), nom: nomTache.trim(), typeBotte, parcelleIds: selected, statut: "ouvert", entrees: [], createdAt: todayISO() };
    setTaches((ts) => [t, ...ts]);
    setOpenId(t.id);
    setNomTache("");
    setSelected([]);
  }
  function closeTache() {
    const totalFait = tache.entrees.reduce((s, e) => s + e.nombre, 0);
    const used = parseFloat(bottesUtilisees) || 0;
    setStock((s) => ({ ...s, mouvements: [...s.mouvements,
      { id: uid(), date: todayISO(), type: "entrée", quantite: totalFait, libelle: `Production — ${tache.nom}` },
      ...(used > 0 ? [{ id: uid(), date: todayISO(), type: "sortie", quantite: used, libelle: `Consommation — ${tache.nom}` }] : []),
    ]}));
    setTaches((ts) => ts.map((x) => (x.id === tache.id ? { ...x, statut: "fermé" } : x)));
    setClosing(false);
    setBottesUtilisees("");
  }

  function exportPdf(t) {
    const byParcelle = {};
    t.entrees.forEach((e) => (byParcelle[e.parcelleId] = (byParcelle[e.parcelleId] || 0) + e.nombre));
    const rows = t.parcelleIds.map((pid) => {
      const p = parcelles.find((x) => x.id === pid);
      return `<tr><td>${p?.nom}</td><td>${byParcelle[pid] || 0} bottes</td></tr>`;
    }).join("");
    const total = Object.values(byParcelle).reduce((s, n) => s + n, 0);
    openPdfWindow(`Tâche pressage — ${t.nom}`, `<div class="meta">${t.typeBotte} · ${t.createdAt}</div>
      <table><thead><tr><th>Parcelle</th><th>Bottes</th></tr></thead><tbody>${rows}</tbody>
      <tfoot><tr class="total-row"><td>TOTAL</td><td>${total} bottes</td></tr></tfoot></table>`);
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Pressage" onBack={onBack} tone="admin" />
      <div className="p-5 space-y-4">
        <BigStat label="Stock total" value={`${fmt(stockTotal, 0)} bottes`} tone={stockTotal <= stock.seuilAlerte ? "alert" : "admin"} />
        {stockTotal <= stock.seuilAlerte && (
          <div className="bg-[#D6483A]/10 text-[#D6483A] rounded-2xl px-4 py-3 text-sm font-bold text-center">⚠ Stock sous le seuil d'alerte</div>
        )}

        <Card className="p-5 space-y-3">
          <div className="font-extrabold text-sm text-[#1C2B1E]/50">Ouvrir une tâche</div>
          <BigInput value={nomTache} onChange={(e) => setNomTache(e.target.value)} placeholder="Nom de la tâche" className="text-base text-left" />
          <PillChoice tone="admin" columns={2} value={typeBotte} onChange={setTypeBotte} options={[{ value: "Paille", label: "Paille" }, { value: "Foin", label: "Foin" }]} />
          <div className="flex flex-wrap gap-2">
            {parcelles.map((p) => (
              <button key={p.id} onClick={() => setSelected((s) => (s.includes(p.id) ? s.filter((x) => x !== p.id) : [...s, p.id]))}
                className={`px-4 py-3 rounded-xl text-sm font-bold ${selected.includes(p.id) ? "bg-[#C97B3D] text-white" : "bg-[#F5F0E6] text-[#1C2B1E]/70"}`}>
                {p.nom}
              </button>
            ))}
          </div>
          <ActionButton tone="admin" onClick={createTache} disabled={!nomTache.trim() || selected.length === 0}>+ Créer la tâche</ActionButton>
        </Card>

        {taches.length > 0 && (
          <div className="flex gap-2 overflow-x-auto pb-1">
            {taches.map((t) => (
              <button key={t.id} onClick={() => setOpenId(t.id)} className={`px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap ${openId === t.id ? "bg-[#1C2B1E] text-white" : "bg-white text-[#1C2B1E]/60"}`}>
                {t.nom} {t.statut === "fermé" && "✓"}
              </button>
            ))}
          </div>
        )}

        {tache && (() => {
          const byParcelle = {};
          tache.entrees.forEach((e) => (byParcelle[e.parcelleId] = (byParcelle[e.parcelleId] || 0) + e.nombre));
          const total = Object.values(byParcelle).reduce((s, n) => s + n, 0);
          return (
            <Card className="p-5">
              <div className="flex items-center justify-between mb-3">
                <div className="font-extrabold text-lg">{tache.nom}</div>
                <span className={`text-xs font-bold px-2 py-1 rounded-full ${tache.statut === "ouvert" ? "bg-[#4A7C3F]/15 text-[#4A7C3F]" : "bg-[#1C2B1E]/10"}`}>{tache.statut.toUpperCase()}</span>
              </div>
              <div className="flex justify-between font-extrabold text-xl mb-4">
                <span>Total journée</span><span className="text-[#C97B3D]">{total} bottes</span>
              </div>
              {!closing ? (
                <div className="grid grid-cols-2 gap-2">
                  <ActionButton tone="ghost" size="md" onClick={() => exportPdf(tache)}>📄 Export PDF</ActionButton>
                  {tache.statut === "ouvert" && <ActionButton tone="ghost" size="md" onClick={() => setClosing(true)}>Fermer</ActionButton>}
                </div>
              ) : (
                <div className="space-y-3">
                  <BigInput type="number" value={bottesUtilisees} onChange={(e) => setBottesUtilisees(e.target.value)} placeholder="Bottes utilisées aujourd'hui" />
                  <ActionButton tone="admin" onClick={closeTache}>Confirmer la fermeture</ActionButton>
                  <button className="block w-full text-center text-sm font-bold text-[#1C2B1E]/40 py-1" onClick={() => setClosing(false)}>Annuler</button>
                </div>
              )}
            </Card>
          );
        })()}
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: PRESSAGE — CHAUFFEUR
   ============================================================ */
function PressageDriverModule({ taches, setTaches, parcelles, driverName, onBack }) {
  const open = taches.filter((t) => t.statut === "ouvert");
  const [activeId, setActiveId] = useState(open[0]?.id || null);
  const tache = taches.find((t) => t.id === activeId);
  const [parcelleId, setParcelleId] = useState(tache?.parcelleIds[0] || "");
  const [nombre, setNombre] = useState("");
  const [confirm, setConfirm] = useState(false);

  const mine = (tache?.entrees || []).filter((e) => e.chauffeur === driverName);
  const totalMine = mine.reduce((s, e) => s + e.nombre, 0);

  function valider() {
    if (!nombre || !tache) return;
    setTaches((ts) => ts.map((t) => t.id === tache.id
      ? { ...t, entrees: [...t.entrees, { id: uid(), parcelleId, chauffeur: driverName, nombre: parseInt(nombre, 10) }] }
      : t));
    setNombre("");
    setConfirm(true);
    setTimeout(() => setConfirm(false), 1500);
  }

  if (open.length === 0) {
    return (
      <div className="screen-in min-h-screen bg-[#F5F0E6]">
        <ScreenHeader title="Pressage" onBack={onBack} tone="driver" />
        <p className="text-center text-base text-[#1C2B1E]/45 py-16 px-6">Aucune tâche ouverte pour le moment.</p>
      </div>
    );
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Pressage" onBack={onBack} tone="driver" />
      <div className="p-5 space-y-4">
        {open.length > 1 && (
          <div className="flex gap-2 overflow-x-auto">
            {open.map((t) => (
              <button key={t.id} onClick={() => { setActiveId(t.id); setParcelleId(t.parcelleIds[0] || ""); }}
                className={`px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap ${activeId === t.id ? "bg-[#1C2B1E] text-white" : "bg-white text-[#1C2B1E]/60"}`}>
                {t.nom}
              </button>
            ))}
          </div>
        )}

        <Card className="p-5 space-y-4">
          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Parcelle</div>
            <div className="flex flex-wrap gap-2">
              {tache?.parcelleIds.map((pid) => {
                const p = parcelles.find((x) => x.id === pid);
                return (
                  <button key={pid} onClick={() => setParcelleId(pid)} className={`px-4 py-3 rounded-xl text-sm font-bold ${parcelleId === pid ? "bg-[#1C2B1E] text-white" : "bg-[#F5F0E6] text-[#1C2B1E]/70"}`}>
                    {p?.nom}
                  </button>
                );
              })}
            </div>
          </div>
          <div>
            <div className="text-sm font-bold text-[#1C2B1E]/50 mb-2">Nombre de bottes faites</div>
            <BigInput type="number" inputMode="numeric" value={nombre} onChange={(e) => setNombre(e.target.value)} placeholder="0" />
          </div>
          <ActionButton tone="driver" onClick={valider} disabled={!nombre}>{confirm ? "✓ Enregistré !" : "Valider"}</ActionButton>
        </Card>

        <Card className="p-5 !bg-[#1C2B1E] text-white text-center">
          <div className="text-sm opacity-60">Mon total aujourd'hui</div>
          <div className="text-4xl font-extrabold mt-1">{totalMine} bottes</div>
        </Card>
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: PHYTOSANITAIRE — CHAUFFEUR (juste la photo du bon de livraison)
   ============================================================ */
function PhytoDriverModule({ phyto, setPhyto, onBack }) {
  const [confirme, setConfirme] = useState(false);

  function ajouterDepuisBon(produits) {
    setPhyto((p) => ({ ...p, mouvements: [...p.mouvements, ...produits.map((x) => (
      { id: uid(), date: todayISO(), type: "entrée", produit: x.produit.trim(), quantite: x.quantite, unite: "unités", libelle: "Bon de livraison (photo)" }
    ))] }));
    setConfirme(true);
    setTimeout(() => setConfirme(false), 2000);
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Phytosanitaire" onBack={onBack} tone="driver" />
      <div className="p-5 space-y-4">
        {confirme && (
          <div className="bg-[#4A7C3F]/10 text-[#4A7C3F] rounded-2xl px-4 py-3 text-sm font-bold text-center">✓ Produits ajoutés au stock de la ferme</div>
        )}
        <BonLivraisonImport tone="driver" onValider={ajouterDepuisBon} />
        <p className="text-center text-xs text-[#1C2B1E]/40 px-4">Pour voir le stock complet ou faire l'inventaire, rendez-vous dans « Mes stocks ».</p>
      </div>
    </div>
  );
}

/* ============================================================
   COMPOSANT: STOCK ANNUEL (visible toute l'année + export PDF)
   ============================================================ */
function StockAnnuelView({ title, unite, mouvements, soldeActuel, seuilAlerte, readOnly = false }) {
  const years = listYears(mouvements);
  const [year, setYear] = useState(years[0]);
  const byMonth = useMemo(() => groupByMonth(mouvements, year), [mouvements, year]);
  const monthKeys = Object.keys(byMonth).sort().reverse();
  const [expanded, setExpanded] = useState({});

  const totalEntrees = monthKeys.reduce((s, mk) => s + byMonth[mk].entrees, 0);
  const totalSorties = monthKeys.reduce((s, mk) => s + byMonth[mk].sorties, 0);

  function exportPdf() {
    const rowsSynth = monthKeys.map((mk) =>
      `<tr><td>${monthLabel(mk)}</td><td>+${fmt(byMonth[mk].entrees, 0)}</td><td>−${fmt(byMonth[mk].sorties, 0)}</td><td><b>${fmt(byMonth[mk].soldeFin, 0)}</b></td></tr>`
    ).join("");
    const detail = monthKeys.map((mk) => {
      const rows = byMonth[mk].mouvements.map((m) =>
        `<tr><td>${m.date}</td><td>${m.libelle || m.produit || "—"}</td><td>${m.type}</td><td>${m.type === "sortie" ? "−" : "+"}${fmt(m.quantite, 1)} ${m.unite || unite}</td></tr>`
      ).join("");
      return `<h3 style="margin-top:20px;font-size:14px;border-bottom:1px solid #ddd;">${monthLabel(mk)}</h3>
        <table><thead><tr><th>Date</th><th>Libellé</th><th>Type</th><th>Quantité</th></tr></thead><tbody>${rows}</tbody></table>`;
    }).join("");
    openPdfWindow(`${title} — ${year}`,
      `<div class="meta">Solde actuel : ${fmt(soldeActuel, 0)} ${unite}</div>
       <h2 style="font-size:15px;margin-top:18px;">Synthèse mensuelle</h2>
       <table><thead><tr><th>Mois</th><th>Entrées</th><th>Sorties</th><th>Solde</th></tr></thead><tbody>${rowsSynth || "<tr><td colspan=4>Aucun mouvement.</td></tr>"}</tbody></table>
       <h2 style="font-size:15px;margin-top:24px;">Détail jour par jour</h2>${detail || "<p>Aucun détail.</p>"}`);
  }

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between gap-2">
        <select value={year} onChange={(e) => setYear(e.target.value)} className="px-4 py-2.5 rounded-xl border-2 border-[#1C2B1E]/10 bg-white text-sm font-bold">
          {years.map((y) => <option key={y} value={y}>{y}</option>)}
        </select>
        <button onClick={exportPdf} className="px-4 py-2.5 rounded-xl bg-[#F5F0E6] text-sm font-bold active:bg-[#ece4d2]">📄 PDF</button>
      </div>

      <div className="flex gap-2">
        <BigStat label="Stock actuel" value={`${fmt(soldeActuel, 0)} ${unite}`} tone={seuilAlerte != null && soldeActuel <= seuilAlerte ? "alert" : "field"} />
      </div>
      <div className="flex gap-2">
        <BigStat label={`Entrées ${year}`} value={`+${fmt(totalEntrees, 0)}`} tone="field" />
        <BigStat label={`Sorties ${year}`} value={`−${fmt(totalSorties, 0)}`} tone="driver" />
      </div>

      {monthKeys.length === 0 && <p className="text-center text-sm text-[#1C2B1E]/40 py-6">Aucun mouvement pour {year}.</p>}

      <div className="space-y-2">
        {monthKeys.map((mk) => (
          <Card key={mk} className="overflow-hidden">
            <button onClick={() => setExpanded((e) => ({ ...e, [mk]: !e[mk] }))} className="w-full flex items-center justify-between px-4 py-3.5 text-left">
              <div>
                <div className="font-bold">{monthLabel(mk)}</div>
                <div className="text-xs text-[#1C2B1E]/45">+{fmt(byMonth[mk].entrees, 0)} · −{fmt(byMonth[mk].sorties, 0)} · solde {fmt(byMonth[mk].soldeFin, 0)} {unite}</div>
              </div>
              <span className="text-[#1C2B1E]/30">{expanded[mk] ? "▲" : "▼"}</span>
            </button>
            {expanded[mk] && (
              <div className="border-t border-[#1C2B1E]/8 px-4 py-2 space-y-1">
                {byMonth[mk].mouvements.map((m) => (
                  <div key={m.id} className="flex justify-between text-sm py-1.5 border-b border-[#1C2B1E]/5 last:border-0">
                    <span>{m.date} · {m.libelle || m.produit}</span>
                    <span className={`font-bold ${m.type === "sortie" ? "text-[#D6483A]" : "text-[#4A7C3F]"}`}>{m.type === "sortie" ? "−" : "+"}{fmt(m.quantite, 1)} {m.unite || unite}</span>
                  </div>
                ))}
              </div>
            )}
          </Card>
        ))}
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: PHYTOSANITAIRE — ADMIN (plus de prompt(), champ inline)
   ============================================================ */
function PhytoModule({ phyto, setPhyto, onBack }) {
  const [tab, setTab] = useState("produits"); // produits | azote | inventaire
  const [produit, setProduit] = useState("");
  const [quantite, setQuantite] = useState("");
  const [azoteQte, setAzoteQte] = useState("");

  const stockTotal = phyto.mouvements.reduce((s, m) => s + (m.type === "entrée" ? m.quantite : -m.quantite), 0);
  const azoteTotal = (phyto.azoteMouvements || []).reduce((s, m) => s + (m.type === "entrée" ? m.quantite : -m.quantite), 0);

  // Stock théorique par produit, pour l'inventaire / check des stocks
  const stockParProduit = useMemo(() => {
    const byProduit = {};
    phyto.mouvements.forEach((m) => {
      const key = m.produit || "Sans nom";
      byProduit[key] = (byProduit[key] || 0) + (m.type === "entrée" ? m.quantite : -m.quantite);
    });
    return Object.entries(byProduit).map(([produit, theorique]) => ({ produit, theorique }));
  }, [phyto.mouvements]);

  function addEntree() {
    if (!produit.trim() || !quantite) return;
    setPhyto((p) => ({ ...p, mouvements: [...p.mouvements, { id: uid(), date: todayISO(), type: "entrée", produit: produit.trim(), quantite: parseFloat(quantite), unite: "unités" }] }));
    setProduit(""); setQuantite("");
  }
  function addAzote(type) {
    const q = parseFloat(azoteQte);
    if (!Number.isFinite(q) || q <= 0) return;
    setPhyto((p) => ({ ...p, azoteMouvements: [...(p.azoteMouvements || []), { id: uid(), date: todayISO(), type, quantite: q, libelle: type === "entrée" ? "Livraison azote" : "Épandage azote" }] }));
    setAzoteQte("");
  }
  function ajouterDepuisBon(produits) {
    setPhyto((p) => ({ ...p, mouvements: [...p.mouvements, ...produits.map((x) => (
      { id: uid(), date: todayISO(), type: "entrée", produit: x.produit.trim(), quantite: x.quantite, unite: "unités", libelle: "Bon de livraison (photo)" }
    ))] }));
  }
  function enregistrerInventaire(lignes) {
    // Crée un mouvement de régularisation pour chaque écart constaté (positif = entrée, négatif = sortie)
    const regularisations = lignes
      .filter((l) => l.ecart !== 0)
      .map((l) => ({
        id: uid(),
        date: todayISO(),
        type: l.ecart > 0 ? "entrée" : "sortie",
        produit: l.produit,
        quantite: Math.abs(l.ecart),
        unite: "unités",
        libelle: "Régularisation inventaire",
      }));
    if (regularisations.length > 0) {
      setPhyto((p) => ({ ...p, mouvements: [...p.mouvements, ...regularisations] }));
    }
  }

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Phytosanitaire" onBack={onBack} tone="admin" />
      <div className="p-5 space-y-4">
        <PillChoice tone="admin" columns={3} value={tab} onChange={setTab} options={[{ value: "produits", label: "Produits" }, { value: "azote", label: "Azote" }, { value: "inventaire", label: "Inventaire" }]} />

        {tab === "produits" && (
          <>
            <Card className="p-5 space-y-3">
              <div className="font-extrabold text-sm text-[#1C2B1E]/50">Ajouter une entrée à la main</div>
              <BigInput value={produit} onChange={(e) => setProduit(e.target.value)} placeholder="Nom du produit" className="text-base text-left" />
              <BigInput type="number" inputMode="decimal" value={quantite} onChange={(e) => setQuantite(e.target.value)} placeholder="Quantité" />
              <ActionButton tone="admin" onClick={addEntree} disabled={!produit.trim() || !quantite}>+ Enregistrer</ActionButton>
            </Card>
            <BonLivraisonImport tone="admin" onValider={ajouterDepuisBon} />
            <StockAnnuelView title="Stock phytosanitaire" unite="unités" mouvements={phyto.mouvements} soldeActuel={stockTotal} seuilAlerte={null} />
          </>
        )}

        {tab === "azote" && (
          <>
            <Card className="p-5 space-y-3">
              <BigInput type="number" inputMode="decimal" value={azoteQte} onChange={(e) => setAzoteQte(e.target.value)} placeholder="Quantité (L)" />
              <div className="grid grid-cols-2 gap-2">
                <ActionButton tone="primary" size="md" onClick={() => addAzote("entrée")} disabled={!azoteQte}>+ Livraison</ActionButton>
                <ActionButton tone="danger" size="md" onClick={() => addAzote("sortie")} disabled={!azoteQte}>− Épandage</ActionButton>
              </div>
            </Card>
            <Card className="p-4 flex items-center justify-between">
              <span className="text-sm font-bold">Seuil d'alerte</span>
              <BigInput type="number" inputMode="decimal" value={phyto.azoteSeuil} onChange={(e) => setPhyto((p) => ({ ...p, azoteSeuil: parseFloat(e.target.value) || 0 }))} className="!w-28 text-base py-2" />
            </Card>
            {azoteTotal <= phyto.azoteSeuil && (
              <div className="bg-[#D6483A]/10 text-[#D6483A] rounded-2xl px-4 py-3 text-sm font-bold text-center">⚠ Niveau d'azote sous le seuil</div>
            )}
            <StockAnnuelView title="Azote citerne" unite="L" mouvements={phyto.azoteMouvements || []} soldeActuel={azoteTotal} seuilAlerte={phyto.azoteSeuil} />
          </>
        )}

        {tab === "inventaire" && (
          <StockInventaire tone="admin" produitsTheoriques={stockParProduit} onValiderInventaire={enregistrerInventaire} />
        )}
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: FACTURATION (lignes simplifiées, TVA par défaut 20%)
   ============================================================ */
const TVA_TAUX = [{ l: "20%", v: 0.2 }, { l: "10%", v: 0.1 }, { l: "5,5%", v: 0.055 }, { l: "0%", v: 0 }];

function stockSources(stockPaille, phyto) {
  const pailleTotal = stockPaille.mouvements.reduce((s, m) => s + (m.type === "entrée" ? m.quantite : -m.quantite), 0);
  const phytoTotal = phyto.mouvements.reduce((s, m) => s + (m.type === "entrée" ? m.quantite : -m.quantite), 0);
  const azoteTotal = (phyto.azoteMouvements || []).reduce((s, m) => s + (m.type === "entrée" ? m.quantite : -m.quantite), 0);
  return [
    { key: "paille", label: "Paille / foin", unite: "bottes", disponible: pailleTotal },
    { key: "phyto", label: "Phytosanitaire", unite: "unités", disponible: phytoTotal },
    { key: "azote", label: "Azote citerne", unite: "L", disponible: azoteTotal },
  ];
}
function emptyLigne() { return { id: uid(), designation: "", quantite: 1, prixUnitaire: 0, tva: 0.2, lieAuStock: "" }; }

function FacturationModule({ stockPaille, setStockPaille, phyto, setPhyto, factures, setFactures, onBack }) {
  const [clientType, setClientType] = useState("particulier"); // particulier | professionnel
  const [client, setClient] = useState("");
  const [siren, setSiren] = useState("");
  const [numTva, setNumTva] = useState("");
  const [lignes, setLignes] = useState([emptyLigne()]);
  const [openId, setOpenId] = useState(factures[0]?.id || null);
  const sources = stockSources(stockPaille, phyto);

  function updateLigne(id, patch) { setLignes((ls) => ls.map((l) => (l.id === id ? { ...l, ...patch } : l))); }
  const totaux = useMemo(() => {
    let ht = 0; const tvaParTaux = {};
    lignes.forEach((l) => {
      const m = (parseFloat(l.quantite) || 0) * (parseFloat(l.prixUnitaire) || 0);
      ht += m;
      const t = parseFloat(l.tva) || 0;
      tvaParTaux[t] = (tvaParTaux[t] || 0) + m * t;
    });
    const totalTva = Object.values(tvaParTaux).reduce((s, v) => s + v, 0);
    return { ht, tvaParTaux, ttc: ht + totalTva };
  }, [lignes]);

  function creerFacture() {
    if (!client.trim() || lignes.every((l) => !l.designation)) return;
    const numero = `F-${new Date().getFullYear()}-${String(factures.length + 1).padStart(3, "0")}`;
    const dateFacture = todayISO();
    setStockPaille((s) => ({ ...s, mouvements: [...s.mouvements, ...lignes.filter((l) => l.lieAuStock === "paille" && l.designation).map((l) =>
      ({ id: uid(), date: dateFacture, type: "sortie", quantite: parseFloat(l.quantite) || 0, libelle: `Facture ${numero} — ${client}` }))] }));
    setPhyto((p) => ({ ...p,
      mouvements: [...p.mouvements, ...lignes.filter((l) => l.lieAuStock === "phyto" && l.designation).map((l) =>
        ({ id: uid(), date: dateFacture, type: "sortie", quantite: parseFloat(l.quantite) || 0, produit: l.designation, unite: "unités" }))],
      azoteMouvements: [...(p.azoteMouvements || []), ...lignes.filter((l) => l.lieAuStock === "azote" && l.designation).map((l) =>
        ({ id: uid(), date: dateFacture, type: "sortie", quantite: parseFloat(l.quantite) || 0, libelle: `Facture ${numero}` }))],
    }));
    const facture = {
      id: uid(), numero, date: dateFacture,
      clientType, client: client.trim(),
      siren: clientType === "professionnel" ? siren.trim() : "",
      numTva: clientType === "professionnel" ? numTva.trim() : "",
      lignes: lignes.filter((l) => l.designation), totaux,
    };
    setFactures((fs) => [facture, ...fs]);
    setOpenId(facture.id);
    setClientType("particulier");
    setClient("");
    setSiren("");
    setNumTva("");
    setLignes([emptyLigne()]);
  }

  function exportFacturePdf(f) {
    const rows = f.lignes.map((l) => `<tr><td>${l.designation}</td><td>${fmt(l.quantite, 2)}</td><td>${fmt(l.prixUnitaire, 2)} €</td><td>${fmt((parseFloat(l.quantite) || 0) * (parseFloat(l.prixUnitaire) || 0), 2)} €</td></tr>`).join("");
    const clientInfo = f.clientType === "professionnel"
      ? `${f.client}${f.siren ? ` — SIREN ${f.siren}` : ""}${f.numTva ? ` — TVA ${f.numTva}` : ""}`
      : f.client;
    openPdfWindow(`Facture ${f.numero}`, `<div class="meta">${f.date} — ${clientInfo}</div>
      <table><thead><tr><th>Désignation</th><th>Qté</th><th>Prix</th><th>Montant</th></tr></thead><tbody>${rows}</tbody></table>
      <table style="margin-top:16px;max-width:300px;margin-left:auto;"><tr><td>Total HT</td><td>${fmt(f.totaux.ht, 2)} €</td></tr><tr class="total-row"><td>TOTAL TTC</td><td>${fmt(f.totaux.ttc, 2)} €</td></tr></table>`);
  }

  const facture = factures.find((f) => f.id === openId);

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Facturation" onBack={onBack} tone="admin" />
      <div className="p-5 space-y-4">
        <Card className="p-5 space-y-4">
          <div className="font-extrabold text-sm text-[#1C2B1E]/50">Nouvelle facture</div>
          <PillChoice
            tone="admin"
            columns={2}
            value={clientType}
            onChange={setClientType}
            options={[{ value: "particulier", label: "Particulier" }, { value: "professionnel", label: "Professionnel" }]}
          />
          <BigInput value={client} onChange={(e) => setClient(e.target.value)} placeholder={clientType === "professionnel" ? "Nom de l'entreprise" : "Nom du client"} className="text-base text-left" />
          {clientType === "professionnel" && (
            <div className="grid grid-cols-2 gap-2">
              <BigInput
                value={siren}
                onChange={(e) => setSiren(e.target.value.replace(/\D/g, "").slice(0, 9))}
                placeholder="N° SIREN"
                inputMode="numeric"
                className="text-sm text-left"
              />
              <BigInput
                value={numTva}
                onChange={(e) => setNumTva(e.target.value.toUpperCase())}
                placeholder="N° TVA intracom."
                className="text-sm text-left"
              />
            </div>
          )}
          {lignes.map((l) => (
            <Card key={l.id} className="p-3 space-y-2 !bg-[#F5F0E6]/50">
              <BigInput value={l.designation} onChange={(e) => updateLigne(l.id, { designation: e.target.value })} placeholder="Désignation" className="text-sm text-left py-2.5" />
              <select value={l.lieAuStock} onChange={(e) => { const src = sources.find((s) => s.key === e.target.value); updateLigne(l.id, { lieAuStock: e.target.value, designation: e.target.value ? (l.designation || src.label) : l.designation }); }}
                className="w-full px-3 py-2.5 rounded-xl border-2 border-[#1C2B1E]/10 bg-white text-sm font-semibold">
                <option value="">Ligne libre</option>
                {sources.map((s) => <option key={s.key} value={s.key}>Déduire : {s.label} ({fmt(s.disponible, 0)} {s.unite})</option>)}
              </select>
              <div className="grid grid-cols-3 gap-2">
                <BigInput type="number" inputMode="decimal" value={l.quantite} onChange={(e) => updateLigne(l.id, { quantite: e.target.value })} placeholder="Qté" className="text-sm py-2.5" />
                <BigInput type="number" inputMode="decimal" value={l.prixUnitaire} onChange={(e) => updateLigne(l.id, { prixUnitaire: e.target.value })} placeholder="Prix €" className="text-sm py-2.5" />
                <select value={l.tva} onChange={(e) => updateLigne(l.id, { tva: parseFloat(e.target.value) })} className="px-2 py-2.5 rounded-xl border-2 border-[#1C2B1E]/10 bg-white text-sm font-bold">
                  {TVA_TAUX.map((t) => <option key={t.v} value={t.v}>{t.l}</option>)}
                </select>
              </div>
            </Card>
          ))}
          <button className="block w-full text-center text-sm font-bold text-[#4A7C3F] py-2" onClick={() => setLignes((ls) => [...ls, emptyLigne()])}>+ Ajouter une ligne</button>
          <Card className="p-4 !bg-[#1C2B1E] text-white">
            <div className="flex justify-between font-extrabold text-lg"><span>TOTAL TTC</span><span>{fmt(totaux.ttc, 2)} €</span></div>
          </Card>
          <ActionButton tone="admin" onClick={creerFacture} disabled={!client.trim() || lignes.every((l) => !l.designation)}>Créer la facture</ActionButton>
        </Card>

        {factures.length > 0 && (
          <div className="flex gap-2 overflow-x-auto pb-1">
            {factures.map((f) => (
              <button key={f.id} onClick={() => setOpenId(f.id)} className={`px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap ${openId === f.id ? "bg-[#1C2B1E] text-white" : "bg-white text-[#1C2B1E]/60"}`}>{f.numero}</button>
            ))}
          </div>
        )}

        {facture && (
          <Card className="p-5">
            <div className="flex justify-between items-center mb-3">
              <div>
                <div className="font-extrabold">{facture.numero}</div>
                <div className="text-xs text-[#1C2B1E]/45">{facture.date} · {facture.client}</div>
                {facture.clientType === "professionnel" && (
                  <div className="text-xs text-[#1C2B1E]/45">
                    {facture.siren && `SIREN ${facture.siren}`}
                    {facture.siren && facture.numTva && " · "}
                    {facture.numTva && `TVA ${facture.numTva}`}
                  </div>
                )}
              </div>
              <span className="font-extrabold text-xl text-[#C97B3D]">{fmt(facture.totaux.ttc, 2)} €</span>
            </div>
            <ActionButton tone="ghost" size="md" onClick={() => exportFacturePdf(facture)}>📄 Export PDF</ActionButton>
          </Card>
        )}
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: INFOS (cours + entraide) — partagé admin/chauffeur
   ============================================================ */
function InfosModule({ infos, setInfos, readOnly, onBack, tone }) {
  const [tab, setTab] = useState("cours");
  function updateCours(id, patch) { setInfos((i) => ({ ...i, cours: i.cours.map((c) => (c.id === id ? { ...c, ...patch, maj: todayISO() } : c)) })); }
  function addEntraide() { setInfos((i) => ({ ...i, entraide: [...i.entraide, { id: uid(), titre: "", description: "", contact: "" }] })); }
  function updateEntraide(id, patch) { setInfos((i) => ({ ...i, entraide: i.entraide.map((e) => (e.id === id ? { ...e, ...patch } : e)) })); }
  function removeEntraide(id) { setInfos((i) => ({ ...i, entraide: i.entraide.filter((e) => e.id !== id) })); }

  const categories = ["Céréale", "Engrais", "Carburant"];

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Infos & cours" onBack={onBack} tone={tone} />
      <div className="p-5 space-y-4">
        <PillChoice tone={tone} columns={2} value={tab} onChange={setTab} options={[{ value: "cours", label: "Cours" }, { value: "entraide", label: "Entraide" }]} />

        {tab === "cours" && categories.map((cat) => (
          <div key={cat}>
            <div className="font-extrabold mb-2">{cat}</div>
            <div className="space-y-2">
              {infos.cours.filter((c) => c.categorie === cat).map((c) => (
                <Card key={c.id} className="p-4 flex items-center justify-between">
                  {readOnly ? (
                    <div className="font-bold">{c.nom}</div>
                  ) : (
                    <BigInput value={c.nom} onChange={(e) => updateCours(c.id, { nom: e.target.value })} className="text-sm text-left py-2 flex-1 mr-2" />
                  )}
                  <div className="text-lg font-extrabold text-[#4A7C3F] whitespace-nowrap">
                    {readOnly ? `${fmt(c.valeur, 2)} ${c.unite}` : (
                      <input type="number" step="0.01" value={c.valeur} onChange={(e) => updateCours(c.id, { valeur: parseFloat(e.target.value) || 0 })} className="w-20 text-right font-extrabold border-b-2 border-[#4A7C3F]/30" />
                    )}
                  </div>
                </Card>
              ))}
            </div>
          </div>
        ))}

        {tab === "entraide" && (
          <div className="space-y-3">
            {infos.entraide.map((e) => (
              <Card key={e.id} className="p-4 space-y-2">
                {readOnly ? (
                  <>
                    <div className="font-bold">{e.titre}</div>
                    <p className="text-sm text-[#1C2B1E]/65">{e.description}</p>
                    <div className="text-xs text-[#1C2B1E]/40">{e.contact}</div>
                  </>
                ) : (
                  <>
                    <BigInput value={e.titre} onChange={(ev) => updateEntraide(e.id, { titre: ev.target.value })} placeholder="Titre" className="text-sm text-left py-2.5" />
                    <textarea value={e.description} onChange={(ev) => updateEntraide(e.id, { description: ev.target.value })} placeholder="Description" rows={2}
                      className="w-full px-4 py-3 rounded-xl border-2 border-[#1C2B1E]/10 text-sm" />
                    <div className="flex gap-2">
                      <BigInput value={e.contact} onChange={(ev) => updateEntraide(e.id, { contact: ev.target.value })} placeholder="Contact" className="text-sm text-left py-2.5 flex-1" />
                      <button onClick={() => removeEntraide(e.id)} className="w-10 h-10 rounded-xl bg-[#D6483A]/10 text-[#D6483A] font-bold flex-shrink-0">✕</button>
                    </div>
                  </>
                )}
              </Card>
            ))}
            {!readOnly && <button className="block w-full text-center text-sm font-bold text-[#4A7C3F] py-2" onClick={addEntraide}>+ Ajouter</button>}
            {infos.entraide.length === 0 && readOnly && <p className="text-center text-sm text-[#1C2B1E]/40 py-8">Aucune information pour le moment.</p>}
          </div>
        )}
      </div>
    </div>
  );
}

/* ============================================================
   CHAUFFEUR: MES STOCKS — historique perso + stocks ferme
   ============================================================ */
function DriverStocksView({ driverName, parcelles, ensilageChantiers, epandageChantiers, moissonChantiers, pressageTaches, stockPaille, phyto, onBack }) {
  const [section, setSection] = useState("perso");

  const mesPeseesEnsilage = useMemo(() => {
    const out = [];
    ensilageChantiers.forEach((c) => c.pesees.filter((p) => p.chauffeur === driverName).forEach((p) => {
      const parc = parcelles.find((x) => x.id === p.parcelleId);
      out.push({ id: p.id, date: c.createdAt, type: "entrée", quantite: p.net, unite: "kg", libelle: `${c.nom} (${parc?.nom || "?"})` });
    }));
    return out;
  }, [ensilageChantiers, parcelles, driverName]);

  const mesPeseesEpandage = useMemo(() => {
    const out = [];
    epandageChantiers.forEach((c) => c.pesees.filter((p) => p.chauffeur === driverName).forEach((p) => {
      const parc = parcelles.find((x) => x.id === p.parcelleId);
      out.push({ id: p.id, date: c.createdAt, type: "entrée", quantite: p.net, unite: "kg", libelle: `${c.nom} (${parc?.nom || "?"})` });
    }));
    return out;
  }, [epandageChantiers, parcelles, driverName]);

  const mesPeseesMoisson = useMemo(() => {
    const out = [];
    moissonChantiers.forEach((c) => c.pesees.filter((p) => p.chauffeur === driverName).forEach((p) => {
      const parc = parcelles.find((x) => x.id === p.parcelleId);
      out.push({ id: p.id, date: c.createdAt, type: "entrée", quantite: p.net, unite: "kg", libelle: `${c.nom} (${parc?.nom || "?"})` });
    }));
    return out;
  }, [moissonChantiers, parcelles, driverName]);

  const mesBottes = useMemo(() => {
    const out = [];
    pressageTaches.forEach((t) => t.entrees.filter((e) => e.chauffeur === driverName).forEach((e) => {
      const parc = parcelles.find((x) => x.id === e.parcelleId);
      out.push({ id: e.id, date: t.createdAt, type: "entrée", quantite: e.nombre, unite: "bottes", libelle: `${t.nom} (${parc?.nom || "?"})` });
    }));
    return out;
  }, [pressageTaches, parcelles, driverName]);

  const totalEnsilage = mesPeseesEnsilage.reduce((s, m) => s + m.quantite, 0);
  const totalEpandage = mesPeseesEpandage.reduce((s, m) => s + m.quantite, 0);
  const totalMoisson = mesPeseesMoisson.reduce((s, m) => s + m.quantite, 0);
  const totalBottes = mesBottes.reduce((s, m) => s + m.quantite, 0);
  const stockPailleTotal = stockPaille.mouvements.reduce((s, m) => s + (m.type === "entrée" ? m.quantite : -m.quantite), 0);
  const stockPhytoTotal = phyto.mouvements.reduce((s, m) => s + (m.type === "entrée" ? m.quantite : -m.quantite), 0);
  const stockAzoteTotal = (phyto.azoteMouvements || []).reduce((s, m) => s + (m.type === "entrée" ? m.quantite : -m.quantite), 0);

  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Mes stocks" onBack={onBack} tone="driver" />
      <div className="p-5 space-y-4">
        <PillChoice
          tone="driver"
          columns={2}
          value={section}
          onChange={setSection}
          options={[
            { value: "perso", label: "Mon activité" },
            { value: "paille", label: "Paille/foin" },
            { value: "phyto", label: "Phyto" },
            { value: "azote", label: "Azote" },
          ]}
        />

        {section === "perso" && (
          <div className="space-y-4">
            <div className="flex gap-2">
              <BigStat label="Ensilage" value={`${fmt(totalEnsilage, 0)} kg`} tone="field" />
              <BigStat label="Épandage" value={`${fmt(totalEpandage, 0)} kg`} tone="field" />
              <BigStat label="Moisson" value={`${fmt(totalMoisson, 0)} kg`} tone="field" />
              <BigStat label="Bottes" value={totalBottes} tone="field" />
            </div>
            {mesPeseesEnsilage.length > 0 && <StockAnnuelView title="Ensilage" unite="kg" mouvements={mesPeseesEnsilage} soldeActuel={totalEnsilage} seuilAlerte={null} readOnly />}
            {mesPeseesEpandage.length > 0 && <StockAnnuelView title="Épandage" unite="kg" mouvements={mesPeseesEpandage} soldeActuel={totalEpandage} seuilAlerte={null} readOnly />}
            {mesPeseesMoisson.length > 0 && <StockAnnuelView title="Moisson" unite="kg" mouvements={mesPeseesMoisson} soldeActuel={totalMoisson} seuilAlerte={null} readOnly />}
            {mesBottes.length > 0 && <StockAnnuelView title="Pressage" unite="bottes" mouvements={mesBottes} soldeActuel={totalBottes} seuilAlerte={null} readOnly />}
            {mesPeseesEnsilage.length === 0 && mesPeseesEpandage.length === 0 && mesPeseesMoisson.length === 0 && mesBottes.length === 0 && (
              <p className="text-center text-sm text-[#1C2B1E]/40 py-10">Aucune activité enregistrée pour le moment.</p>
            )}
          </div>
        )}
        {section === "paille" && <StockAnnuelView title="Paille / foin (ferme)" unite="bottes" mouvements={stockPaille.mouvements} soldeActuel={stockPailleTotal} seuilAlerte={stockPaille.seuilAlerte} readOnly />}
        {section === "phyto" && <StockAnnuelView title="Phytosanitaire (ferme)" unite="unités" mouvements={phyto.mouvements} soldeActuel={stockPhytoTotal} seuilAlerte={null} readOnly />}
        {section === "azote" && <StockAnnuelView title="Azote citerne (ferme)" unite="L" mouvements={phyto.azoteMouvements || []} soldeActuel={stockAzoteTotal} seuilAlerte={phyto.azoteSeuil} readOnly />}
      </div>
    </div>
  );
}

/* ============================================================
   MODULE: PARAMÈTRES — réglages du compte (pont bascule, ...)
   ============================================================ */
function ParametresModule({ pontBascule, onChangePontBascule, onBack }) {
  return (
    <div className="screen-in min-h-screen bg-[#F5F0E6] pb-10">
      <ScreenHeader title="Paramètres" onBack={onBack} tone="admin" />
      <div className="p-5 space-y-4">
        <Card className="p-5 space-y-3">
          <div className="font-extrabold text-sm text-[#1C2B1E]/50">Pont bascule</div>
          <p className="text-xs text-[#1C2B1E]/45">
            Les modules Ensilage et Épandage nécessitent un pont bascule pour peser les chargements. Activez cette option si l'exploitation en dispose.
          </p>
          <PillChoice
            tone="admin"
            columns={2}
            value={pontBascule ? "oui" : "non"}
            onChange={(v) => onChangePontBascule(v === "oui")}
            options={[{ value: "oui", label: "Oui" }, { value: "non", label: "Non" }]}
          />
        </Card>
      </div>
    </div>
  );
}

/* ============================================================
   APP PRINCIPALE
   ============================================================ */
export default function App() {
  const [screen, setScreen] = useState("splash"); // splash | login | roleSelect | adminApp | driverApp
  const [accounts, setAccounts] = useState(() => loadStoredAccounts());
  const [currentAccount, setCurrentAccount] = useState(null);
  const [driverName, setDriverName] = useState("");

  const [parcelles, setParcelles] = useState(initialParcelles);
  const [ensilageChantiers, setEnsilageChantiers] = useState([]);
  const [epandageChantiers, setEpandageChantiers] = useState([]);
  const [moissonChantiers, setMoissonChantiers] = useState([]);
  const [pressageTaches, setPressageTaches] = useState([]);
  const [stockPaille, setStockPaille] = useState({ seuilAlerte: 500, mouvements: [] });
  const [phyto, setPhyto] = useState({ azoteSeuil: 500, azoteMouvements: [], mouvements: [] });
  const [factures, setFactures] = useState([]);
  const [infos, setInfos] = useState({ cours: COURS_DEFAUT, entraide: [] });

  const [adminModule, setAdminModule] = useState(null);
  const [driverModule, setDriverModule] = useState(null);

  useEffect(() => {
    document.getElementById("root")?.scrollTo({ top: 0 });
    window.scrollTo({ top: 0 });
  }, [screen, adminModule, driverModule]);

  function handleCreateAccount(acc) {
    setAccounts((a) => {
      const next = [...a, acc];
      saveStoredAccounts(next);
      return next;
    });
  }
  function updatePontBascule(value) {
    setCurrentAccount((acc) => {
      if (!acc) return acc;
      const updated = { ...acc, pontBascule: value };
      setAccounts((all) => {
        const next = all.map((a) => (a.email === acc.email ? updated : a));
        saveStoredAccounts(next);
        return next;
      });
      return updated;
    });
  }
  function logout() {
    setDriverName("");
    setDriverModule(null);
    setAdminModule(null);
    setScreen("login");
  }
  function backToRoleSelect() {
    setDriverName("");
    setDriverModule(null);
    setAdminModule(null);
    setScreen("roleSelect");
  }

  if (screen === "splash") return <SplashScreen onContinue={() => setScreen("login")} />;

  if (screen === "login")
    return <LoginScreen accounts={accounts} onLogin={(acc) => { setCurrentAccount(acc); setScreen("roleSelect"); }} onCreateAccount={handleCreateAccount} />;

  if (screen === "roleSelect")
    return (
      <RoleSelectScreen
        adminCode={currentAccount?.adminCode || ""}
        onEnterAdmin={() => setScreen("adminApp")}
        onEnterDriver={(name) => { setDriverName(name); setScreen("driverApp"); }}
      />
    );

  if (screen === "adminApp") {
    if (!adminModule) return <AdminHome onOpen={setAdminModule} onLogout={logout} onSwitchRole={backToRoleSelect} pontBascule={currentAccount?.pontBascule} />;
    const back = () => setAdminModule(null);
    if (adminModule === "parcelles") return <ParcellesModule parcelles={parcelles} setParcelles={setParcelles} onBack={back} />;
    if (adminModule === "ensilage" && currentAccount?.pontBascule) return <EnsilageAdminModule parcelles={parcelles} chantiers={ensilageChantiers} setChantiers={setEnsilageChantiers} onBack={back} />;
    if (adminModule === "epandage" && currentAccount?.pontBascule) return <EpandageAdminModule parcelles={parcelles} chantiers={epandageChantiers} setChantiers={setEpandageChantiers} onBack={back} />;
    if (adminModule === "moisson") return <MoissonAdminModule parcelles={parcelles} chantiers={moissonChantiers} setChantiers={setMoissonChantiers} onBack={back} />;
    if (adminModule === "pressage") return <PressageAdminModule parcelles={parcelles} taches={pressageTaches} setTaches={setPressageTaches} stock={stockPaille} setStock={setStockPaille} onBack={back} />;
    if (adminModule === "phyto") return <PhytoModule phyto={phyto} setPhyto={setPhyto} onBack={back} />;
    if (adminModule === "facturation") return <FacturationModule stockPaille={stockPaille} setStockPaille={setStockPaille} phyto={phyto} setPhyto={setPhyto} factures={factures} setFactures={setFactures} onBack={back} />;
    if (adminModule === "infos") return <InfosModule infos={infos} setInfos={setInfos} readOnly={false} onBack={back} tone="admin" />;
    if (adminModule === "parametres") return <ParametresModule pontBascule={currentAccount?.pontBascule} onChangePontBascule={updatePontBascule} onBack={back} />;
  }

  if (screen === "driverApp") {
    if (!driverModule) return <DriverHome driverName={driverName} onOpen={setDriverModule} onLogout={logout} onSwitchRole={backToRoleSelect} pontBascule={currentAccount?.pontBascule} />;
    const back = () => setDriverModule(null);
    if (driverModule === "ensilage" && currentAccount?.pontBascule) return <EnsilageDriverModule chantiers={ensilageChantiers} setChantiers={setEnsilageChantiers} parcelles={parcelles} driverName={driverName} onBack={back} />;
    if (driverModule === "epandage" && currentAccount?.pontBascule) return <EpandageDriverModule chantiers={epandageChantiers} setChantiers={setEpandageChantiers} parcelles={parcelles} driverName={driverName} onBack={back} />;
    if (driverModule === "moisson") return <MoissonDriverModule chantiers={moissonChantiers} setChantiers={setMoissonChantiers} parcelles={parcelles} driverName={driverName} onBack={back} />;
    if (driverModule === "pressage") return <PressageDriverModule taches={pressageTaches} setTaches={setPressageTaches} parcelles={parcelles} driverName={driverName} onBack={back} />;
    if (driverModule === "phyto") return <PhytoDriverModule phyto={phyto} setPhyto={setPhyto} onBack={back} />;
    if (driverModule === "stocks") return <DriverStocksView driverName={driverName} parcelles={parcelles} ensilageChantiers={ensilageChantiers} epandageChantiers={epandageChantiers} moissonChantiers={moissonChantiers} pressageTaches={pressageTaches} stockPaille={stockPaille} phyto={phyto} onBack={back} />;
    if (driverModule === "infos") return <InfosModule infos={infos} setInfos={setInfos} readOnly onBack={back} tone="driver" />;
  }

  return null;
}
