document.addEventListener('DOMContentLoaded', () => {
    const participantsTbody = document.getElementById('participants-tbody');
    const tableHeaders = document.querySelectorAll('#participants-table th[data-column]');

    let currentParticipants = []; // Store fetched data
    let currentSortColumn = 'name'; // Default sort
    let currentSortDirection = 'asc';

    // --- Data Fetching and Rendering ---

    async function loadParticipants() {
        try {
            const response = await fetch('/api/participants');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            currentParticipants = await response.json();
            sortParticipants(); // Sort initially
            renderTable();
        } catch (error) {
            console.error('Error loading participants:', error);
            participantsTbody.innerHTML = '<tr><td colspan="4">Error loading participants. Please try again later.</td></tr>';
        }
    }

    function renderTable() {
        participantsTbody.innerHTML = ''; // Clear current table body

        if (!currentParticipants || currentParticipants.length === 0) {
            participantsTbody.innerHTML = '<tr><td colspan="4">No participants signed up yet.</td></tr>';
            return;
        }

        // Update sort indicators in headers
        updateSortIndicators();

        currentParticipants.forEach(participant => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${escapeHtml(participant.name)}</td>
                <td>${escapeHtml(participant.lessonType)}</td>
                <td>${escapeHtml(participant.contact || '')}</td>
                <td>
                    <button data-id="${participant.id}" class="remove-btn">Remove</button>
                </td>
            `;
            participantsTbody.appendChild(tr);
        });

        // Add event listeners to new remove buttons
        addRemoveButtonListeners();
    }

    // --- Sorting Logic ---

    function sortParticipants() {
        currentParticipants.sort((a, b) => {
            const valA = a[currentSortColumn]?.toLowerCase() || ''; // Handle null/undefined
            const valB = b[currentSortColumn]?.toLowerCase() || '';

            let comparison = 0;
            if (valA > valB) {
                comparison = 1;
            } else if (valA < valB) {
                comparison = -1;
            }
            return (currentSortDirection === 'desc') ? (comparison * -1) : comparison;
        });
    }

    function handleHeaderClick(event) {
        const column = event.target.closest('th').getAttribute('data-column');
        if (!column) return; // Clicked on Actions header or non-data header

        if (column === currentSortColumn) {
            // Reverse direction
            currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            // Change column, default to ascending
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
                arrowSpan.textContent = ''; // Or a default faint arrow like '↕'
                arrowSpan.style.opacity = '0.5';
            }
        });
    }

    // Add listeners to headers
    tableHeaders.forEach(th => th.addEventListener('click', handleHeaderClick));

    // --- Remove Logic ---

    async function removeParticipant(id) {
        if (!confirm('Are you sure you want to remove this participant?')) {
            return;
        }
        try {
            const response = await fetch(`/api/participants/${id}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                 // Attempt to get error message from server if available
                 let errorMsg = `HTTP error! status: ${response.status}`;
                 try {
                     // Try to parse potential JSON error response from controller
                     const errorData = await response.json();
                     errorMsg = errorData.error || errorData.message || errorMsg;
                 } catch (e) { /* Ignore if body is not JSON or empty */ }
                 throw new Error(errorMsg);
            }

            // Successfully deleted, reload the data and table
            await loadParticipants(); // Re-fetch and re-render
        } catch (error) {
            console.error('Error removing participant:', error);
            alert(`Failed to remove participant: ${error.message}`);
        }
    }

    function addRemoveButtonListeners() {
        const removeButtons = participantsTbody.querySelectorAll('.remove-btn');
        removeButtons.forEach(button => {
            button.addEventListener('click', (event) => {
                // Get the Firestore document ID (String)
                const idToRemove = event.target.getAttribute('data-id');
                if (idToRemove) { // Check if ID exists
                    removeParticipant(idToRemove);
                }
            });
        });
    }

    // --- Utility ---
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

    // --- Initial Load ---
    loadParticipants();
}); 