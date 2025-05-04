// Use React hooks for state and effects
const { useState, useEffect } = React;

function App() {
    // State for form inputs
    const [name, setName] = useState('');
    const [lessonType, setLessonType] = useState('');
    const [contact, setContact] = useState('');

    // State for submission status/feedback
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitStatus, setSubmitStatus] = useState({ message: '', type: '' }); // type: 'success' or 'error'

    const handleSubmit = async (event) => {
        event.preventDefault();
        setIsSubmitting(true);
        setSubmitStatus({ message: '', type: '' }); // Clear previous status

        if (!name || !lessonType) {
            setSubmitStatus({ message: 'Please fill in Name and Lesson Type.', type: 'error' });
            setIsSubmitting(false);
            return;
        }

        const participantData = {
            name: name,
            lessonType: lessonType,
            contact: contact
        };

        try {
            const response = await fetch('/api/participants', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(participantData),
            });

            if (!response.ok) {
                const errorText = response.statusText || `Server error (Status: ${response.status})`;
                throw new Error(errorText);
            }

            // Success
            setSubmitStatus({ message: 'Sign-up successful! Thank you.', type: 'success' });
            // Clear form
            setName('');
            setLessonType('');
            setContact('');

        } catch (error) {
            console.error('Error adding participant:', error);
            setSubmitStatus({ message: `Failed to add participant: ${error.message}. Please try again.`, type: 'error' });
        } finally {
            setIsSubmitting(false);
        }
    };

    // Simple JSX structure for the form
    return (
        <div className="app-container"> {/* Added container for potential background image */} 
            <header>
                <h1>Hopedale Parks Tennis Lessons</h1>
                <p>Sign up for Adult & Child Sessions</p>
            </header>
            <main>
                <section className="signup-section">
                    <h2>Sign Up Now!</h2>
                    <form onSubmit={handleSubmit} className="signup-form">
                        <div className="form-group">
                            <label htmlFor="name">Full Name:</label>
                            <input
                                type="text"
                                id="name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                required
                                disabled={isSubmitting}
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="lesson-type">Lesson Type:</label>
                            <select
                                id="lesson-type"
                                value={lessonType}
                                onChange={(e) => setLessonType(e.target.value)}
                                required
                                disabled={isSubmitting}
                            >
                                <option value="" disabled>-- Select Type --</option>
                                <option value="adult">Adult</option>
                                <option value="child">Child</option>
                            </select>
                        </div>
                        <div className="form-group">
                            <label htmlFor="contact">Contact Info (Email/Phone):</label>
                            <input
                                type="text"
                                id="contact"
                                value={contact}
                                onChange={(e) => setContact(e.target.value)}
                                disabled={isSubmitting}
                                placeholder="Optional, for reminders/updates"
                            />
                        </div>

                        <button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? 'Submitting...' : 'Sign Up'}
                        </button>

                        {submitStatus.message && (
                            <div className={`submit-message ${submitStatus.type}`}>
                                {submitStatus.message}
                            </div>
                        )}
                    </form>
                </section>
            </main>
            <footer>
                <p>Hopedale Parks Program</p>
            </footer>
        </div>
    );
}

// Mount the App component to the DOM
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />); 