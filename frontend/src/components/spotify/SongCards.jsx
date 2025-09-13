const SongCards = ({ spotifyData }) => {
  if (!spotifyData) return null;

  return (
    <div
      style={{
        display: "flex",
        flexWrap: "wrap",
        gap: "1rem",
        justifyContent: "center",
      }}
    >
      {spotifyData.map(({ name, artist, img, album }, index) => (
        <div
          key={index}
          style={{
            border: "1px solid #000000ff",
            borderRadius: "10px",
            padding: "0.5rem",
            width: "120px",
            textAlign: "center",
            boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
            wordWrap: "break-word",  
            overflowWrap: "break-word",
            hyphens: "auto",        
          }}
        >
          <img
            src={img}
            alt={`${album} cover`}
            style={{
              width: "100%",
              borderRadius: "8px",
              marginBottom: "0.5rem",
              objectFit: "cover",
            }}
          />
          <h4
            style={{
              fontSize: "1.0rem",
              margin: "0.3rem 0",
              lineHeight: "1.2",
              overflowWrap: "break-word",
            }}
          >
            {name}
          </h4>
          <p
            style={{
              margin: 0,
              fontSize: "0.8rem",
              lineHeight: "1.2",
              overflowWrap: "break-word",
            }}
          >
            <strong>Artist:</strong> {artist}
          </p>
        </div>
      ))}
    </div>
  );
};

export default SongCards;

