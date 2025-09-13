const prepareCountryBarData = (data) => {
  const countryCount = {};
  data.forEach(({ country }) => {
    if (country) {
      countryCount[country] = (countryCount[country] || 0) + 1;
    }
  });

  return Object.entries(countryCount).map(([country, count]) => ({
    country,
    count,
  }));
};

export default prepareCountryBarData;
