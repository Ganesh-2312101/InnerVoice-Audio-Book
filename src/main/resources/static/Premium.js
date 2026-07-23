    async function UpgradePremium(plan){
      const params= new URLSearchParams(window.location.search);
      const id = params.get('userId') || localStorage.getItem('userId');
      const response=await fetch(`/users/upgrade/${id}?plan=${plan}`,{
        method : 'PUT'
      });
      if(response.ok){
        alert(`Successfully upgraded user ${id} to ${plan} plan!`);
        window.location.href = `Profile.html?userId=${id}`;
      } else {
        alert('Failed to upgrade. Please try again later.');
      }
    }
