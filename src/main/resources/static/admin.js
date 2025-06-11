document.addEventListener('DOMContentLoaded', () => {
    const participantsTbody = document.getElementById('participants-tbody');
    const tableHeaders = document.querySelectorAll('#participants-table th[data-column]');

    let currentParticipants = [];
    let currentSortColumn = 'parentName';
    let currentSortDirection = 'asc';

    async function loadParticipants() {
        try {
            const response = await fetch('/api/participants');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            currentParticipants = await response.json();
            sortParticipants();
            renderTable();
        } catch (error) {
            console.error('Error loading participants:', error);
            participantsTbody.innerHTML = '<tr><td colspan="6">Error loading participants. Please try again later.</td></tr>';
        }
    }

    function renderTable() {
        participantsTbody.innerHTML = '';

        if (!currentParticipants || currentParticipants.length === 0) {
            participantsTbody.innerHTML = '<tr><td colspan="6">No participants signed up yet.</td></tr>';
            return;
        }

        updateSortIndicators();

        currentParticipants.forEach(p => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${escapeHtml(p.parentName)}</td>
                <td>${escapeHtml(p.parentEmail)}</td>
                <td>${escapeHtml(p.childName)}</td>
                <td>${escapeHtml(p.lessonType)}</td>
                <td>${escapeHtml(p.lessonSession)}</td>
                <td><button data-id="${p.id}" class="remove-btn">Remove</button></td>
            `;
            participantsTbody.appendChild(tr);
        });

        addRemoveButtonListeners();
    }

    function sortParticipants() {
        currentParticipants.sort((a, b) => {
            const valA = a[currentSortColumn]?.toLowerCase() || '';
            const valB = b[currentSortColumn]?.toLowerCase() || '';

            let comparison = 0;
            if (valA > valB) comparison = 1;
            else if (valA < valB) comparison = -1;

            return currentSortDirection === 'desc' ? -comparison : comparison;
        });
    }

    function handleHeaderClick(event) {
        const column = event.target.closest('th').getAttribute('data-column');
        if (!column) return;

        if (column === currentSortColumn) {
            currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            currentSortColumn = column;
            currentSortDirection = 'asc';
        }

        sortParticipants();
        renderTable();
    }

    function updateSortIndicators() {
        tableHeaders.forEach(th => {
            const arrowSpan = th.querySelector('.sort-arrow');
            const column = th.getAttribute('data-column');
            if (column === currentSortColumn) {
                arrowSpan.textContent = currentSortDirection === 'asc' ? '▲' : '▼';
                arrowSpan.style.opacity = '1';
            } else {
                arrowSpan.textContent = '';
                arrowSpan.style.opacity = '0.5';
            }
        });
    }

    async function removeParticipant(id) {
        if (!confirm('Are you sure you want to remove this participant?')) return;

        try {
            const response = await fetch(`/api/participants/${id}`, { method: 'DELETE' });

            if (!response.ok) {
                let errorMsg = `HTTP error! status: ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.error || errorData.message || errorMsg;
                } catch (e) { }
                throw new Error(errorMsg);
            }

            await loadParticipants();
        } catch (error) {
            console.error('Error removing participant:', error);
            alert(`Failed to remove participant: ${error.message}`);
        }
    }

    function addRemoveButtonListeners() {
        const removeButtons = participantsTbody.querySelectorAll('.remove-btn');
        removeButtons.forEach(button => {
            button.addEventListener('click', e => {
                const idToRemove = e.target.getAttribute('data-id');
                if (idToRemove) removeParticipant(idToRemove);
            });
        });
    }

    function escapeHtml(unsafe) {
        if (unsafe === null || unsafe === undefined) return '';
        return unsafe
            .toString()
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    tableHeaders.forEach(th => th.addEventListener('click', handleHeaderClick));

    loadParticipants();
});
