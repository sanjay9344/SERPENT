/**
 * SERPENT. — Main JavaScript (Matches UI Photos)
 * Handles species dataset rendering, filtering, details modal, and session checks.
 */

// Exact 8 Snake Species Dataset matching Image 4
const SERPENT_SPECIES = [
  {
    id: 'indian-cobra',
    name: 'Indian Cobra',
    latin: 'Naja naja',
    isVenomous: true,
    country: 'India',
    description: 'Recognized by its expanded hood, this iconic snake inhabits a wide range of landscapes.',
    image: 'assets/images/indian-cobra.jpg',
    subcaption: '',
    habitat: 'Farmlands, grasslands, village peripheries, and open woods across India.',
    safetyNote: 'Defensive flaring of the hood indicates agitation. Maintain a distance of at least 10 feet. Seek immediate ASV if bitten.'
  },
  {
    id: 'common-krait',
    name: 'Common Krait',
    latin: 'Bungarus caeruleus',
    isVenomous: true,
    country: 'India',
    description: 'A nocturnal snake with a glossy body and narrow light-colored bands.',
    image: 'assets/images/common-krait.jpg',
    subcaption: '',
    habitat: 'Scrub jungles, fields, and rustic dwellings. Highly active during night hours.',
    safetyNote: 'Nocturnal envenomations are often painless. Sleep on elevated beds with mosquito nets.'
  },
  {
    id: 'russells-viper',
    name: "Russell's Viper",
    latin: 'Daboia russelii',
    isVenomous: true,
    country: 'India',
    description: 'A heavy-bodied viper with distinctive oval markings along its back.',
    image: 'assets/images/russells-viper.jpg',
    subcaption: '',
    habitat: 'Grassy plains, thorn forests, and crop fields. Avoids thick forests.',
    safetyNote: 'Emits a high-pitched acoustic warning hiss when threatened. Reacts swiftly to provocation.'
  },
  {
    id: 'indian-rat-snake',
    name: 'Indian Rat Snake',
    latin: 'Ptyas mucosa',
    isVenomous: false,
    country: 'India',
    description: 'An agile, non-venomous snake that plays an important role in controlling rodents.',
    image: 'assets/images/indian-rat-snake.jpg',
    subcaption: '',
    habitat: 'Agricultural areas, gardens, wetlands, and urban peripheries.',
    safetyNote: 'Harmless to humans. Vital for natural rodent pest control. Never kill or harm.'
  },
  {
    id: 'saw-scaled-viper',
    name: 'Saw-scaled Viper',
    latin: 'Echis carinatus',
    isVenomous: true,
    country: 'India',
    description: 'A small viper adapted to dry terrain, known for its defensive rasping sound.',
    image: 'assets/images/saw-scaled-viper.jpg',
    subcaption: 'Representative viper image, not species identification',
    habitat: 'Arid zones, rocky soil, open farmlands, and dry deciduous scrub.',
    safetyNote: 'Rubs side scales together to produce a rasping warning. Watch your step in scrublands.'
  },
  {
    id: 'eastern-brown-snake',
    name: 'Eastern Brown Snake',
    latin: 'Pseudonaja textilis',
    isVenomous: true,
    country: 'Australia',
    description: 'A fast-moving Australian snake found in open habitats.',
    image: 'assets/images/eastern-brown.jpg',
    subcaption: 'Representative snake image, not species identification',
    habitat: 'Dry open grasslands, pastures, and farmland across eastern Australia.',
    safetyNote: 'Fast moving and alert. Highly venomous neurotoxic/coagulant venom.'
  },
  {
    id: 'corn-snake',
    name: 'Corn Snake',
    latin: 'Pantherophis guttatus',
    isVenomous: false,
    country: 'United States',
    description: 'A non-venomous constrictor native to the southeastern United States.',
    image: 'assets/images/corn-snake.jpg',
    subcaption: 'Representative snake image, not species identification',
    habitat: 'Wooded groves, pine forest, and abandoned agricultural buildings.',
    safetyNote: 'Completely harmless to humans. Known for docile temperament.'
  },
  {
    id: 'boa-constrictor',
    name: 'Boa Constrictor',
    latin: 'Boa constrictor',
    isVenomous: false,
    country: 'Brazil',
    description: 'A large, non-venomous constrictor found in varied tropical environments.',
    image: 'assets/images/boa-constrictor.jpg',
    subcaption: 'Representative snake image, not species identification',
    habitat: 'Tropical rainforests, woodlands, and humid riparian corridors.',
    safetyNote: 'Non-venomous constrictor. Ambush predator active near water bodies.'
  }
];

document.addEventListener('DOMContentLoaded', () => {
  renderSpecies(SERPENT_SPECIES);
  initFilters();
  initEmergencyModal();
  initDetailsModal();
  checkAuthSession();
});

/**
 * Render species cards in 4-column layout matching Image 4
 */
function renderSpecies(items) {
  const grid = document.getElementById('species-cards-grid');
  const countEl = document.getElementById('species-count');
  if (!grid) return;

  if (countEl) countEl.textContent = `${items.length} species shown`;

  if (items.length === 0) {
    grid.innerHTML = `
      <div style="grid-column: 1 / -1; text-align: center; padding: 48px; border: 1px dashed var(--color-border); border-radius: 10px;">
        <h4 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 6px;">No matching snakes found</h4>
        <p style="color: var(--color-text-secondary); margin-bottom: 16px;">Try adjusting your keyword or country filter.</p>
        <button type="button" class="btn-signup-teal" id="btn-clear-empty-filter">Reset Filters</button>
      </div>
    `;
    const resetBtn = document.getElementById('btn-clear-empty-filter');
    if (resetBtn) resetBtn.addEventListener('click', resetAllFilters);
    return;
  }

  grid.innerHTML = items.map(snake => {
    const badgeDot = snake.isVenomous ? 'dot-venomous' : 'dot-non-venomous';
    const badgeClass = snake.isVenomous ? 'is-venomous' : 'is-non-venomous';
    const badgeText = snake.isVenomous ? 'Venomous' : 'Non-venomous';

    const subcaptionHtml = snake.subcaption
      ? `<p class="card-subcaption">${snake.subcaption}</p>`
      : '';

    return `
      <article class="snake-card-serpent" data-id="${snake.id}">
        <div class="card-image-box">
          <img src="${snake.image}" alt="${snake.name}" loading="lazy">
          <span class="badge-pill-floating ${badgeClass}">
            <span class="${badgeDot}"></span> ${badgeText}
          </span>
        </div>
        
        <div class="card-content-serpent">
          <div class="card-title-row">
            <h3 class="snake-title">${snake.name}</h3>
            <span class="card-arrow-icon">↗</span>
          </div>
          
          <div class="snake-latin">${snake.latin}</div>
          <p class="snake-desc">${snake.description}</p>

          <div class="card-footer-serpent">
            <span class="card-location">📍 ${snake.country}</span>
            <button type="button" class="card-details-btn" data-detail-id="${snake.id}">
              View details ↗
            </button>
          </div>
          ${subcaptionHtml}
        </div>
      </article>
    `;
  }).join('');

  // Attach modal triggers
  const detailButtons = grid.querySelectorAll('[data-detail-id]');
  detailButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const id = btn.getAttribute('data-detail-id');
      const item = SERPENT_SPECIES.find(s => s.id === id);
      if (item) showSpeciesDetails(item);
    });
  });
}

/**
 * Search & Filters (Hero capsule + Section tabs)
 */
function initFilters() {
  const searchInput = document.getElementById('hero-search-input');
  const countrySelect = document.getElementById('hero-country-select');
  const classificationSelect = document.getElementById('hero-classification-select');
  const exploreBtn = document.getElementById('btn-hero-explore');
  const tabButtons = document.querySelectorAll('.filter-tab-btn');

  let activeTab = 'all';

  function filterData() {
    const query = searchInput ? searchInput.value.trim().toLowerCase() : '';
    const selectedCountry = countrySelect ? countrySelect.value : 'all';
    const selectedClass = classificationSelect ? classificationSelect.value : 'all';

    const results = SERPENT_SPECIES.filter(s => {
      // 1. Text search
      const matchesSearch = !query ||
        s.name.toLowerCase().includes(query) ||
        s.latin.toLowerCase().includes(query) ||
        s.description.toLowerCase().includes(query);

      // 2. Country
      const matchesCountry = (selectedCountry === 'all') ||
        (s.country.toLowerCase() === selectedCountry.toLowerCase());

      // 3. Classification (from dropdown or tabs)
      let matchesClass = true;
      const classToCheck = activeTab !== 'all' ? activeTab : selectedClass;
      if (classToCheck === 'venomous') {
        matchesClass = s.isVenomous;
      } else if (classToCheck === 'non-venomous') {
        matchesClass = !s.isVenomous;
      }

      return matchesSearch && matchesCountry && matchesClass;
    });

    renderSpecies(results);
  }

  if (searchInput) searchInput.addEventListener('input', filterData);
  if (countrySelect) countrySelect.addEventListener('change', filterData);
  if (classificationSelect) classificationSelect.addEventListener('change', filterData);
  if (exploreBtn) {
    exploreBtn.addEventListener('click', () => {
      filterData();
      const el = document.getElementById('snakes');
      if (el) el.scrollIntoView({ behavior: 'smooth' });
    });
  }

  tabButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      tabButtons.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      activeTab = btn.getAttribute('data-filter');
      filterData();
    });
  });
}

function resetAllFilters() {
  const searchInput = document.getElementById('hero-search-input');
  const countrySelect = document.getElementById('hero-country-select');
  const classificationSelect = document.getElementById('hero-classification-select');
  const tabButtons = document.querySelectorAll('.filter-tab-btn');

  if (searchInput) searchInput.value = '';
  if (countrySelect) countrySelect.value = 'all';
  if (classificationSelect) classificationSelect.value = 'all';
  tabButtons.forEach(b => b.classList.remove('active'));
  const allTab = document.querySelector('.filter-tab-btn[data-filter="all"]');
  if (allTab) allTab.classList.add('active');

  renderSpecies(SERPENT_SPECIES);
}

/**
 * Emergency Action Protocol Modal
 */
function initEmergencyModal() {
  const modal = document.getElementById('emergency-modal');
  const triggers = document.querySelectorAll('[data-emergency-trigger]');
  const closeBtns = document.querySelectorAll('.close-emergency-modal');

  triggers.forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      if (modal) modal.classList.add('is-active');
    });
  });

  closeBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      if (modal) modal.classList.remove('is-active');
    });
  });

  if (modal) {
    modal.addEventListener('click', (e) => {
      if (e.target === modal) modal.classList.remove('is-active');
    });
  }
}

/**
 * Species Details Modal
 */
function initDetailsModal() {
  const modal = document.getElementById('details-modal');
  const closeBtns = document.querySelectorAll('.close-details-modal');

  closeBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      if (modal) modal.classList.remove('is-active');
    });
  });

  if (modal) {
    modal.addEventListener('click', (e) => {
      if (e.target === modal) modal.classList.remove('is-active');
    });
  }
}

function showSpeciesDetails(snake) {
  const modal = document.getElementById('details-modal');
  const body = document.getElementById('details-modal-body');
  const title = document.getElementById('details-modal-title');
  if (!modal || !body || !title) return;

  title.textContent = `${snake.name} (${snake.latin})`;

  const badgeColor = snake.isVenomous ? '#C62828' : '#2E7D32';
  const badgeDot = snake.isVenomous ? '#E65100' : '#2E7D32';

  body.innerHTML = `
    <div style="width: 100%; aspect-ratio: 16/9; overflow: hidden; border-radius: 8px; margin-bottom: 16px;">
      <img src="${snake.image}" alt="${snake.name}" style="width: 100%; height: 100%; object-fit: cover;">
    </div>
    <div style="display: flex; gap: 8px; align-items: center; margin-bottom: 12px;">
      <span style="font-weight: 700; color: ${badgeColor}; font-size: 0.85rem; display: inline-flex; align-items: center; gap: 6px;">
        <span style="width: 8px; height: 8px; border-radius: 50%; background-color: ${badgeDot};"></span>
        ${snake.isVenomous ? 'Venomous' : 'Non-venomous'}
      </span>
      <span style="color: var(--color-text-muted); font-size: 0.85rem;">·</span>
      <span style="color: var(--color-text-muted); font-size: 0.85rem;">📍 ${snake.country}</span>
    </div>
    <p style="font-size: 0.95rem; line-height: 1.6; color: var(--color-text-main); margin-bottom: 12px;">
      ${snake.description}
    </p>
    <div style="background: var(--color-bg-subtle); padding: 14px; border-radius: 8px; border-left: 3px solid var(--color-primary); margin-bottom: 12px;">
      <strong style="font-size: 0.85rem; color: var(--color-primary); display: block; margin-bottom: 4px;">HABITAT &amp; DISTRIBUTION:</strong>
      <span style="font-size: 0.88rem; color: var(--color-text-secondary);">${snake.habitat}</span>
    </div>
    <div style="background: ${snake.isVenomous ? 'var(--color-emergency-light)' : 'var(--color-success-light)'}; padding: 14px; border-radius: 8px; border-left: 3px solid ${snake.isVenomous ? 'var(--color-emergency)' : 'var(--color-success)'};">
      <strong style="font-size: 0.85rem; color: ${snake.isVenomous ? 'var(--color-emergency)' : 'var(--color-success)'}; display: block; margin-bottom: 4px;">SAFETY GUIDANCE:</strong>
      <span style="font-size: 0.88rem; color: var(--color-text-main);">${snake.safetyNote}</span>
    </div>
  `;

  modal.classList.add('is-active');
}

/**
 * Check authenticated user session
 */
async function checkAuthSession() {
  const authContainer = document.getElementById('header-auth-actions');
  if (!authContainer) return;

  try {
    const res = await Api.getCurrentUser();
    if (res.ok && res.data && res.data.success && res.data.data) {
      const user = res.data.data;
      const firstName = user.fullName ? user.fullName.split(' ')[0] : 'User';

      authContainer.innerHTML = `
        <div class="btn-user-badge">
          <span>👤</span>
          <span>${firstName}</span>
        </div>
        <button type="button" class="btn-user-logout" id="btn-serpent-signout">Sign Out</button>
      `;

      const signoutBtn = document.getElementById('btn-serpent-signout');
      if (signoutBtn) {
        signoutBtn.addEventListener('click', async () => {
          await Api.logout();
          window.location.reload();
        });
      }
    } else {
      authContainer.innerHTML = `
        <a href="login.html" class="btn-login-text">Log In</a>
        <a href="signup.html" class="btn-signup-teal">Sign Up ↗</a>
      `;
    }
  } catch (e) {
    authContainer.innerHTML = `
      <a href="login.html" class="btn-login-text">Log In</a>
      <a href="signup.html" class="btn-signup-teal">Sign Up ↗</a>
    `;
  }
}
