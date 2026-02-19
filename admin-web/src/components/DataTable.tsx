interface Column { key: string; label: string }
interface Props { columns: Column[]; data: any[] }

export default function DataTable({ columns, data }: Props) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-cyan-500/20">
            {columns.map(c => (
              <th key={c.key} className="text-left py-3 px-4 text-gray-400 font-medium">{c.label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, i) => (
            <tr key={i} className="border-b border-white/5 hover:bg-white/5 transition">
              {columns.map(c => (
                <td key={c.key} className="py-3 px-4 text-gray-300">{String(row[c.key] ?? "-")}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
