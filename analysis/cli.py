"""Simple CLI for the analysis package.

Usage examples:
  python -m analysis.cli summary
  python -m analysis.cli elbow --k 6 --out elbow.png
"""
import argparse
from pathlib import Path
from . import load_deliver_df, plot_elbow_graph, data_summary


def cmd_summary(args):
    df = load_deliver_df()
    summary = data_summary(df)
    for k, v in summary.items():
        print(f"{k}: {v}")


def cmd_elbow(args):
    df = load_deliver_df()
    fig = plot_elbow_graph(df, max_k=args.k)
    out = Path(args.out)
    fig.savefig(str(out), dpi=150)
    print(f"Wrote elbow plot to {out}")


def main():
    p = argparse.ArgumentParser()
    sub = p.add_subparsers(dest='cmd')

    s = sub.add_parser('summary')
    s.set_defaults(func=cmd_summary)

    e = sub.add_parser('elbow')
    e.add_argument('--k', type=int, default=8)
    e.add_argument('--out', default='elbow.png')
    e.set_defaults(func=cmd_elbow)

    args = p.parse_args()
    if not args.cmd:
        p.print_help()
        return
    args.func(args)


if __name__ == '__main__':
    main()
