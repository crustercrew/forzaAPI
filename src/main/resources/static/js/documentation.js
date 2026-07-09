// Submenu toggle functionality
document.addEventListener('DOMContentLoaded', function() {
    const toggleButtons = document.querySelectorAll('.submenu-toggle');

    toggleButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            const targetId = this.getAttribute('data-target');
            const submenu = document.getElementById(targetId);

            // Toggle collapsed state
            submenu.classList.toggle('collapsed');
            this.classList.toggle('collapsed');
        });
    });
});