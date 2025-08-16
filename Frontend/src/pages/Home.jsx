import React, { useState, useEffect, useMemo, useRef } from 'react';
import { Search, RefreshCw } from 'lucide-react';
import { LinePath } from '@visx/shape';
import { scaleTime, scaleLinear } from '@visx/scale';
import { AxisBottom, AxisLeft } from '@visx/axis';

// Helper: fetch intraday quote data for a symbol
async function fetchQuote(symbol, controller) {
    const url = `http://localhost:8080/market/indices`;
    const res = await fetch(url, { signal: controller?.signal });
    if (!res.ok) throw new Error(`Failed to fetch ${symbol}: ${res.status}`);
    return await res.json();
}

// A dedicated component for the sparkline chart using Visx
const Sparkline = ({ data, width, height, margin = { top: 2, right: 2, bottom: 2, left: 2 } }) => {
    const { xScale, yScale, color, sorted } = useMemo(() => {
        const points = Array.isArray(data)
            ? data.filter(d => d && typeof d.time === 'number' && typeof d.value === 'number')
            : [];
        const sorted = points.slice().sort((a, b) => a.time - b.time);
        if (sorted.length === 0) {
            return { xScale: null, yScale: null, color: '#888', sorted: [] };
        }
        const innerWidth = Math.max(0, width - margin.left - margin.right);
        const innerHeight = Math.max(0, height - margin.top - margin.bottom);
        const timeValues = sorted.map(d => new Date(d.time * 1000));
        const valueValues = sorted.map(d => d.value);
        const xScale = scaleTime({ range: [0, innerWidth], domain: [Math.min(...timeValues), Math.max(...timeValues)] });
        const yScale = scaleLinear({ range: [innerHeight, 0], domain: [Math.min(...valueValues), Math.max(...valueValues)] });
        if (typeof xScale.nice === 'function') xScale.nice();
        if (typeof yScale.nice === 'function') yScale.nice();
        const isUp = valueValues[valueValues.length - 1] >= valueValues[0];
        const color = isUp ? '#22c55e' : '#ef4444';
        return { xScale, yScale, color, sorted };
    }, [data, width, height, margin]);

    if (!xScale || !sorted || sorted.length === 0) return null;

    const getX = (d) => xScale(new Date(d.time * 1000));
    const getY = (d) => yScale(d.value);

    return (
        <svg width={width} height={height}>
            <g transform={`translate(${margin.left}, ${margin.top})`}>
                <LinePath data={sorted} x={getX} y={getY} stroke={color} strokeWidth={1.5} />
            </g>
        </svg>
    );
};

// Detail chart with axes and responsive width (line only, no baseline fill)
const DetailChart = ({ data, height = 180, margin = { top: 10, right: 12, bottom: 28, left: 60 } }) => {
    const containerRef = useRef(null);
    const [width, setWidth] = useState(0);

    useEffect(() => {
        const el = containerRef.current;
        if (!el) return;
        const update = () => setWidth(el.clientWidth);
        update();
        if ('ResizeObserver' in window) {
            const ro = new ResizeObserver(update);
            ro.observe(el);
            return () => ro.disconnect();
        } else {
            window.addEventListener('resize', update);
            return () => window.removeEventListener('resize', update);
        }
    }, []);

    const { xScale, yScale, color, innerHeight, sorted } = useMemo(() => {
        const points = Array.isArray(data)
            ? data.filter(d => d && typeof d.time === 'number' && typeof d.value === 'number')
            : [];
        const sorted = points.slice().sort((a, b) => a.time - b.time);
        if (sorted.length === 0 || width === 0) {
            return { xScale: null, yScale: null, color: '#888', innerWidth: 0, innerHeight: 0, sorted: [] };
        }
        const innerWidth = Math.max(0, width - margin.left - margin.right);
        const innerHeight = Math.max(0, height - margin.top - margin.bottom);
        const timeValues = sorted.map(d => new Date(d.time * 1000));
        const valueValues = sorted.map(d => d.value);
        const xScale = scaleTime({ range: [0, innerWidth], domain: [Math.min(...timeValues), Math.max(...timeValues)] });
        const minV = Math.min(...valueValues);
        const maxV = Math.max(...valueValues);
        const pad = (maxV - minV) * 0.05 || 1;
        const yScale = scaleLinear({ range: [innerHeight, 0], domain: [minV - pad, maxV + pad] });
        if (typeof xScale.nice === 'function') xScale.nice();
        if (typeof yScale.nice === 'function') yScale.nice();
        const isUp = valueValues[valueValues.length - 1] >= valueValues[0];
        const color = isUp ? '#22c55e' : '#ef4444';
        return { xScale, yScale, color, innerWidth, innerHeight, sorted };
    }, [data, width, height, margin]);

    if (!xScale || width === 0) {
        return <div ref={containerRef} className="w-full h-[180px]" />;
    }

    const getX = (d) => xScale(new Date(d.time * 1000));
    const getY = (d) => yScale(d.value);

    const numTicksY = Math.max(2, Math.floor((height - margin.top - margin.bottom) / 48));
    const numTicksX = Math.max(2, Math.floor((width - margin.left - margin.right) / 110));
    const yTickFormat = (d) => new Intl.NumberFormat('en-US', { maximumFractionDigits: 2 }).format(d);

    return (
        <div ref={containerRef} className="w-full" style={{ overflow: 'hidden' }}>
            <svg width={width} height={height}>
                <g transform={`translate(${margin.left}, ${margin.top})`}>
                    <LinePath data={sorted} x={getX} y={getY} stroke={color} strokeWidth={1.8} />

                    <AxisLeft
                        scale={yScale}
                        numTicks={numTicksY}
                        stroke="#3f3f46"
                        tickStroke="#52525b"
                        hideZero
                        hideAxisLine
                        tickFormat={yTickFormat}
                        tickLabelProps={() => ({ fill: '#9ca3af', fontSize: 10, textAnchor: 'end', dy: '0.33em', dx: '-0.35em' })}
                    />
                    <AxisBottom
                        top={innerHeight}
                        scale={xScale}
                        numTicks={numTicksX}
                        stroke="#3f3f46"
                        tickStroke="#52525b"
                        hideAxisLine
                        tickFormat={(d) => {
                            try { return new Date(d).toLocaleDateString('en-US', { month: 'short', day: '2-digit' }); } catch { return ''; }
                        }}
                        tickLabelProps={() => ({ fill: '#9ca3af', fontSize: 10, textAnchor: 'middle', dy: '0.75em' })}
                    />
                </g>
            </svg>
        </div>
    );
};

// Whitelist of indices to show (reduced set)
const INDEX_WHITELIST = [
  // Indian Indices
  { symbol: '^NSEI', name: 'NIFTY 50', region: 'India' },
  { symbol: '^BSESN', name: 'BSE SENSEX', region: 'India' },
  { symbol: '^NSEBANK', name: 'NIFTY Bank', region: 'India' },
  { symbol: '^CNXIT', name: 'NIFTY IT', region: 'India' },
  { symbol: '^CNXPHARMA', name: 'NIFTY Pharma', region: 'India' },

  // US Indices
  { symbol: '^GSPC', name: 'S&P 500', region: 'USA' },
  { symbol: '^IXIC', name: 'NASDAQ Composite', region: 'USA' },
  { symbol: '^DJI', name: 'Dow Jones Industrial Average', region: 'USA' },
  { symbol: '^RUT', name: 'Russell 2000', region: 'USA' },
  { symbol: '^VIX', name: 'CBOE Volatility Index', region: 'USA' },

  // European Indices
  { symbol: '^FTSE', name: 'FTSE 100', region: 'UK' },
  { symbol: '^GDAXI', name: 'DAX PERFORMANCE-INDEX', region: 'Germany' },
  { symbol: '^FCHI', name: 'CAC 40', region: 'France' },
  { symbol: '^STOXX50E', name: 'EURO STOXX 50', region: 'Europe' },

  // Asian Indices
  { symbol: '^N225', name: 'Nikkei 225', region: 'Japan' },
  { symbol: '^HSI', name: 'HANG SENG INDEX', region: 'Hong Kong' },
  { symbol: '000001.SS', name: 'SSE Composite Index', region: 'China' },
  { symbol: '^KS11', name: 'KOSPI Composite Index', region: 'South Korea' },
  { symbol: '^TWII', name: 'TSEC weighted index', region: 'Taiwan' },
  { symbol: '^AXJO', name: 'S&P/ASX 200', region: 'Australia' },
  { symbol: '^NZ50', name: 'S&P/NZX 50 Index', region: 'New Zealand' },
  { symbol: '^JKSE', name: 'Jakarta Composite Index', region: 'Indonesia' },
  { symbol: '^STI', name: 'Straits Times Index', region: 'Singapore' },
];

export default function Home() {
    const [marketData, setMarketData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [selected, setSelected] = useState(null);

    // Build list using backend quotes
    const loadAllQuotes = async () => {
        setLoading(true);
        setError(null);
        const ac = new AbortController();
        try {
            const items = await Promise.all(
                INDEX_WHITELIST.map(async (meta) => {
                    try {
                        const raw = await fetchQuote(meta.symbol, ac);
                        // Accept either array or object.chartData
                        const chartData = Array.isArray(raw) ? raw : Array.isArray(raw?.chartData) ? raw.chartData : [];
                        let price = null, change = 0, percentChange = 0;
                        if (Array.isArray(chartData) && chartData.length >= 1) {
                            const last = chartData[chartData.length - 1]?.value;
                            const prev = chartData.length > 1 ? chartData[chartData.length - 2]?.value : null;
                            if (typeof last === 'number') price = last;
                            if (typeof last === 'number' && typeof prev === 'number' && prev !== 0) {
                                change = last - prev;
                                percentChange = (change / prev);
                            }
                        }
                        return {
                            symbol: meta.symbol,
                            name: meta.name,
                            region: meta.region,
                            chartData,
                            lastUpdated: Date.now(),
                            price: price ?? 0,
                            change,
                            percentChange, // fraction
                        };
                    } catch (e) {
                        // On per-symbol failure, still return meta so UI remains usable
                        return {
                            symbol: meta.symbol,
                            name: meta.name,
                            region: meta.region,
                            chartData: [],
                            lastUpdated: Date.now(),
                            price: 0,
                            change: 0,
                            percentChange: 0,
                            error: e.message,
                        };
                    }
                })
            );
            setMarketData(items);
            // default selection
            if (!selected && items.length > 0) setSelected(items[0]);
        } catch (err) {
            setError(err.message || String(err));
        } finally {
            setLoading(false);
        }
        return () => ac.abort();
    };

    // Initial data fetch on component mount
    useEffect(() => {
        loadAllQuotes();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Filter to whitelist and apply search (marketData already merged)
    const filteredData = marketData.filter(item =>
        item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.symbol.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const categorizeIndices = (data) => ({
        'Indian Indices': data.filter(item => item.region === 'India'),
        'US Indices': data.filter(item => item.region === 'USA'),
        'European Indices': data.filter(item => ['UK','Germany','France','Europe'].includes(item.region)),
        'Asian Indices': data.filter(item => ['Japan','Hong Kong','China','South Korea','Taiwan','Australia','New Zealand','Indonesia','Singapore'].includes(item.region)),
    });
    const categorizedData = categorizeIndices(filteredData);

    // Keep selection valid on filter
    useEffect(() => {
        if (!loading && !error) {
            if (!selected && filteredData.length > 0) setSelected(filteredData[0]);
            if (selected && !filteredData.find(i => i.symbol === selected.symbol)) setSelected(filteredData[0] || null);
        }
    }, [loading, error, filteredData, selected]);

    const formatPrice = (price) => new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(price);

    const computeChange = (index) => {
        const points = Array.isArray(index?.chartData) ? index.chartData.filter(p => p && typeof p.value === 'number') : [];
        if (points.length >= 2) {
            const prev = points[points.length - 2].value;
            const last = points[points.length - 1].value;
            if (typeof prev === 'number' && prev !== 0 && typeof last === 'number') {
                const delta = last - prev;
                const pct = (delta / prev) * 100;
                return { absChange: delta, pctChange: pct, isUp: delta >= 0 };
            }
        }
        const fallbackDelta = typeof index?.change === 'number' ? index.change : 0;
        const fallbackPct = typeof index?.percentChange === 'number' ? index.percentChange * 100 : 0;
        return { absChange: fallbackDelta, pctChange: fallbackPct, isUp: fallbackDelta >= 0 };
    };

    const IndexCard = ({ index, onClick, active }) => {
        const { absChange, pctChange, isUp } = computeChange(index);
        return (
            <button
                type="button"
                onClick={() => onClick?.(index)}
                className={`flex-shrink-0 w-52 h-28 rounded-lg p-3 flex flex-col justify-between transition-all duration-300 border ${
                    active ? 'bg-zinc-800/60 border-amber-400/60 ring-1 ring-amber-400/30' : 'bg-zinc-900/50 border-zinc-700/80 hover:border-amber-400/60 hover:bg-zinc-800/50'
                }`}
            >
                <div className="flex items-start">
                    <p className="text-sm font-semibold text-zinc-100 truncate w-full" title={index.name}>{index.name}</p>
                </div>
                <div className="flex items-end justify-between">
                    <div>
                        <p className="text-lg font-bold text-zinc-50">{formatPrice(index.price)}</p>
                        <p className={`text-[11px] ${isUp ? 'text-green-400' : 'text-red-400'}`}>{`${isUp ? '+' : ''}${formatPrice(Math.abs(absChange))} (${pctChange.toFixed(2)}%)`}</p>
                    </div>
                    <div className="w-24 h-8 -mr-1 -mb-1">
                        <Sparkline data={index.chartData} width={96} height={32} />
                    </div>
                </div>
            </button>
        );
    };

    const CategorySection = ({ title, indices }) => {
        if (indices.length === 0) return null;
        return (
            <div className="mb-8">
                <h2 className="text-xl font-bold text-zinc-200 mb-3">{title}</h2>
                <div className="flex gap-4 overflow-x-auto pb-3 custom-scrollbar">
                    {indices.map((index) => (
                        <IndexCard key={index.symbol} index={index} active={selected?.symbol === index.symbol} onClick={setSelected} />
                    ))}
                </div>
            </div>
        );
    };

    const DetailPanel = ({ item }) => {
        if (!item) return (
            <div className="bg-zinc-900/50 border border-zinc-700/80 rounded-lg p-6 h-full flex items-center justify-center text-zinc-500">Select an index to see details</div>
        );
        const { absChange, pctChange, isUp } = computeChange(item);
        return (
            <div className="bg-zinc-900/50 border border-zinc-700/80 rounded-lg p-6">
                <div className="flex items-start justify-between mb-3">
                    <div>
                        <h3 className="text-xl font-bold text-white">{item.name}</h3>
                        <p className="text-xs text-zinc-400">{item.symbol} • {item.region}</p>
                    </div>
                    <div className={`text-sm font-semibold ${isUp ? 'text-green-400' : 'text-red-400'}`}>{`${isUp ? '+' : ''}${formatPrice(Math.abs(absChange))} (${pctChange.toFixed(2)}%)`}</div>
                </div>
                <p className="text-3xl font-bold text-white mb-1">{formatPrice(item.price)}</p>
                <p className="text-xs text-zinc-400 mb-4">{new Date(item.lastUpdated).toLocaleString()}</p>
                <div className="w-full h-[180px] overflow-hidden rounded-md">
                    <DetailChart data={item.chartData} height={180} />
                </div>
            </div>
        );
    };

    return (
        <div className="bg-black text-zinc-300 font-sans min-h-screen overflow-y-auto">
            <main className="relative z-10 mx-auto max-w-[1400px] w-full px-6 py-8 pb-8">
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-white mb-2">Global Market Indices</h1>
                    <div className="relative max-w-lg mt-4">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-500" />
                        <input type="text" placeholder="Search indices..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="w-full pl-10 pr-4 py-2.5 bg-zinc-900 border border-zinc-700 rounded-lg focus:outline-none focus:border-amber-400/80 focus:ring-1 focus:ring-amber-400/50 text-zinc-200 placeholder-zinc-500" />
                    </div>
                </div>

                {loading && (
                    <div className="text-center py-12"><RefreshCw className="w-8 h-8 mx-auto mb-4 animate-spin text-amber-400" /><p className="text-zinc-400">Loading market data...</p></div>
                )}

                {error && (
                    <div className="bg-red-900/20 border border-red-500/30 rounded-lg p-6 text-red-400 text-center mb-8"><p className="font-medium">Error loading market data</p><p className="text-sm mt-1">{error}</p></div>
                )}

                {!loading && !error && (
                    <div className="grid lg:grid-cols-3 gap-8">
                        <div className="lg:col-span-2">
                            <CategorySection title="Indian Indices" indices={categorizedData['Indian Indices']} />
                            <CategorySection title="US Indices" indices={categorizedData['US Indices']} />
                            <CategorySection title="European Indices" indices={categorizedData['European Indices']} />
                            <CategorySection title="Asian Indices" indices={categorizedData['Asian Indices']} />
                        </div>
                        <div className="lg:col-span-1">
                            <DetailPanel item={selected} />
                        </div>
                    </div>
                )}

                {!loading && !error && filteredData.length === 0 && searchTerm && (
                    <div className="text-center py-12"><Search className="w-12 h-12 mx-auto mb-4 text-zinc-600" /><h3 className="text-xl font-semibold text-white mb-2">No indices found</h3><p className="text-zinc-400">Try adjusting your search terms</p></div>
                )}
            </main>

            <style>{`
                .custom-scrollbar::-webkit-scrollbar { height: 6px; }
                .custom-scrollbar::-webkit-scrollbar-track { background: #18181b; border-radius: 3px; }
                .custom-scrollbar::-webkit-scrollbar-thumb { background: #3f3f46; border-radius: 3px; }
                .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #52525b; }
            `}</style>
        </div>
    );
}
