interface Props { label: string; value: string | number; color: string }

export default function StatCard({ label, value, color }: Props) {
  return (
    <div className="bg-surface border border-cyan-500/20 rounded-xl p-4">
      <p className="text-gray-400 text-sm">{label}</p>
      <p className={"text-2xl font-bold mt-1 " + color}>{value}</p>
    </div>
  )
}
