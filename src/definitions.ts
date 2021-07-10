export interface HTTPPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
