# mawa3id Frontend

This is the React frontend for the mawa3id appointment no-show reduction system.

## Requirements
- Node.js 16+
- npm or yarn

## Installation

```bash
npm install
```

## Development

```bash
npm start
```

The application will start on `http://localhost:3000`

## Building

```bash
npm run build
```

The optimized build will be in the `build/` folder.

## Environment Variables

Create a `.env` file in the root directory:

```
REACT_APP_API_URL=http://localhost:8080/api
```

## Features

- User authentication (login/register)
- Services management
- Availability scheduling
- Appointments CRUD
- Analytics dashboard
- Real-time reminders tracking

## Technologies

- React 18
- TailwindCSS
- Zustand (State Management)
- Axios (HTTP Client)
- Recharts (Charting)
- Lucide React (Icons)
- React Router (Navigation)

## Project Structure

- `src/pages/` - Page components
- `src/components/` - Reusable components
- `src/store/` - Zustand stores
- `src/api/` - API client
- `src/hooks/` - Custom React hooks
