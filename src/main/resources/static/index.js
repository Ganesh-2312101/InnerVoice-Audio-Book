let currentSlide = 0;
const slides = document.getElementById('slides');
const totalSlides = 2; // Number of slide groups

setInterval(() => {
  currentSlide = (currentSlide + 1) % totalSlides;
  slides.style.transform = `translateX(-${currentSlide * 100}%)`;
}, 3000);

  let currentSlide1 = 0;
const slides1 = document.getElementById('mobileslides');
const totalSlides1 = 8; // Number of slide groups

setInterval(() => {
  currentSlide1 = (currentSlide1 + 1) % totalSlides1;
  slides1.style.transform = `translateX(-${currentSlide1 * 100}%)`;
}, 3000);
