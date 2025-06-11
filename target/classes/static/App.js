const { useState } = React;

function App() {
    const [parentName, setParentName] = useState('');
    const [parentEmail, setParentEmail] = useState('');
    const [childName, setChildName] = useState('');
    const [lessonType, setLessonType] = useState('');
    const [lessonSession, setLessonSession] = useState('');
    const [contact, setContact] = useState('');

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitStatus, setSubmitStatus] = useState({ message: '', type: '' });

    const handleSubmit = async (event) => {
        event.preventDefault();
        setIsSubmitting(true);
        setSubmitStatus({ message: '', type: '' });

        if (!parentName || !parentEmail || !childName || !lessonType || !lessonSession) {
            setSubmitStatus({ message: 'Please fill in all required fields.', type: 'error' });
            setIsSubmitting(false);
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(parentEmail)) {
            setSubmitStatus({ message: 'Please enter a valid email address.', type: 'error' });
            setIsSubmitting(false);
            return;
        }

        const participantData = {
            parentName,
            parentEmail,
            childName,
            contact,
            lessonType,
            lessonSession
        };

        try {
            const response = await fetch('/api/participants', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(participantData),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || 'Server error');
            }

            setSubmitStatus({ message: 'Sign-up successful! Thank you.', type: 'success' });

            // Reset form
            setParentName('');
            setParentEmail('');
            setChildName('');
            setContact('');
            setLessonType('');
            setLessonSession('');
        } catch (error) {
            console.error('Error adding participant:', error);
            setSubmitStatus({ message: `Failed to add participant: ${error.message}`, type: 'error' });
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="app-container">
            <header>
                <h1>Hopedale Parks Tennis Lessons</h1>
                <p>Sign up for Youth Sessions</p>
            </header>
            <main>
                <section className="signup-section">
                    <h2>Sign Up Now!</h2>
                    <form onSubmit={handleSubmit} className="signup-form">
                        <div className="form-group">
                            <label htmlFor="parent-name">Parent Name:</label>
                            <input
                                type="text"
                                id="parent-name"
                                value={parentName}
                                onChange={(e) => setParentName(e.target.value)}
                                required
                                disabled={isSubmitting}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="parent-email">Parent Email:</label>
                            <input
                                type="email"
                                id="parent-email"
                                value={parentEmail}
                                onChange={(e) => setParentEmail(e.target.value)}
                                required
                                disabled={isSubmitting}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="child-name">Child Name:</label>
                            <input
                                type="text"
                                id="child-name"
                                value={childName}
                                onChange={(e) => setChildName(e.target.value)}
                                required
                                disabled={isSubmitting}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="lesson-type">Lesson Type:</label>
                            <select
                                id="lesson-type"
                                value={lessonType}
                                onChange={(e) => {
                                    setLessonType(e.target.value);
                                    if (e.target.value === "Introduction" && lessonSession === "Session 3") {
                                        setLessonSession('');
                                    }
                                }}
                                required
                                disabled={isSubmitting}
                            >
                                <option value="" disabled>-- Select Type --</option>
                                <option value="Introduction">Introduction</option>
                                <option value="Beginner">Beginner</option>
                                <option value="Intermediate">Intermediate</option>
                                <option value="Advanced">Advanced</option>
                            </select>
                        </div>

                        <div className="form-group">
                            <label htmlFor="lesson-session">Lesson Session:</label>
                            <select
                                id="lesson-session"
                                value={lessonSession}
                                onChange={(e) => setLessonSession(e.target.value)}
                                required
                                disabled={isSubmitting}
                            >
                                <option value="" disabled>-- Select Session --</option>
                                <option value="Session 1">Session 1</option>
                                <option value="Session 2">Session 2</option>
                                {lessonType !== "Introduction" && <option value="Session 3">Session 3</option>}
                            </select>
                        </div>

                        <div className="form-group">
                            <label htmlFor="contact">Optional Phone (or Alt Contact):</label>
                            <input
                                type="text"
                                id="contact"
                                value={contact}
                                onChange={(e) => setContact(e.target.value)}
                                disabled={isSubmitting}
                                placeholder="Phone number or other contact"
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

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
